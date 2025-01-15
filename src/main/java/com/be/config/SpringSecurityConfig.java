package com.be.config;

import com.be.config.custom.CustomAccessDeniedHandler;
import com.be.config.custom.CustomAuthenticationEntryPoint;
import com.be.config.custom.CustomDefaultAccessDeniedHandler;
import com.be.config.oauth2.OAuth2AuthenticationFailureHandler;
import com.be.config.oauth2.OAuth2AuthenticationSuccessHandler;
import com.be.model.dto.oauth2.OAuth2UserPrincipal;
import com.be.service.external.OAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityConfig.class);
    private final UserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomDefaultAccessDeniedHandler customDefaultAccessDeniedHandler;
    private final OAuth2UserService oAuth2UserService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public SpringSecurityConfig(@Qualifier("customUserDetailsService") UserDetailsService customUserDetailsService,
                                PasswordEncoder passwordEncoder,
                                CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                CustomAccessDeniedHandler customAccessDeniedHandler, JwtAuthenticationFilter jwtAuthenticationFilter, CustomDefaultAccessDeniedHandler customDefaultAccessDeniedHandler, OAuth2UserService oAuth2UserService) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customDefaultAccessDeniedHandler = customDefaultAccessDeniedHandler;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(List.of(authenticationProvider()));
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("123456"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("123456"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    public AuthenticationProvider authenticationProvider() {
        LOGGER.info("BEAN authenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LOGGER.info("BEAN filterChain");

        // Config CSRF, XSS, Click jacking and so on
//        http.csrf(AbstractHttpConfigurer::disable);

//        http.httpBasic(Customizer.withDefaults());

        // Config router
//        http.csrf(AbstractHttpConfigurer::disable)
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/sign-up",
                                "/api/v1/auth/login",
                                "/api/v1/auth/verify/resend").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/api/v1/oauth2/**").permitAll()

                        .requestMatchers("/api/tmdb/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/trending/day").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/trending/week").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/{movieId}/trailers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/popular").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/{movieId}/cast").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/{movieId}/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/{movieId}/recommendations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/trailers/latest").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/{movieId}/detail").permitAll()

                        /*Cast*/
                        .requestMatchers("/api/casts/**").permitAll()
                        .requestMatchers("/api/casts/{castId}/detail").permitAll()
                        .requestMatchers("/api/casts/{castId}/movies").permitAll()

                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/resources/public").permitAll()
                        .requestMatchers("/api/v1/public").permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole("USER")
                        .requestMatchers("/api/v1/users/mod/**").hasRole("MODERATOR")
                        .requestMatchers("/api/v1/admins/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authEndpoint ->
                                authEndpoint.baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redirectEndpoint ->
                                redirectEndpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .authenticationManager(authenticationManager());

        // In your SecurityConfig
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Config Exception handler
        List<RequestMatcher> defaultAccessDeniedUserPaths = new ArrayList<>();
        defaultAccessDeniedUserPaths.add(new AntPathRequestMatcher("/api/v1/admins/board", "GET"));
//        defaultAccessDeniedUserPaths.add(new AntPathRequestMatcher("/api/v1/admins/**"));
        http.exceptionHandling(e -> e
                .defaultAccessDeniedHandlerFor(customDefaultAccessDeniedHandler, new AndRequestMatcher(defaultAccessDeniedUserPaths))
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://14.225.210.222:8081", "http://localhost:5173", "https://1924-2001-ee0-543c-18c0-14e9-b1e3-18cd-b3d1.ngrok-free.app"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
