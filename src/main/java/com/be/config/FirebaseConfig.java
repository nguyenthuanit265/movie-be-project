//package com.be.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//
//@Configuration
//@Slf4j
//public class FirebaseConfig {
//    @PostConstruct
//    public void init() {
//        log.info("FirebaseConfig initialized");
//    }
//
//    @Bean
//    public FirebaseApp firebaseApp() {
//        try {
//            Resource resource = new ClassPathResource("firebase-service-account.json");
//            log.info("Looking for firebase config at: {}", resource.getURL());
//
//            if (!resource.exists()) {
//                throw new RuntimeException("firebase-service-account.json not found in resources");
//            }
//
//            log.info("Firebase configuration file found and readable");
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                log.info("Initializing new Firebase App");
//                GoogleCredentials credentials = GoogleCredentials.fromStream(
//                        resource.getInputStream()
//                );
//
//                FirebaseOptions options = FirebaseOptions.builder()
//                        .setCredentials(credentials)
//                        .build();
//
//                FirebaseApp app = FirebaseApp.initializeApp(options);
//                log.info("Firebase App initialized successfully");
//                return app;
//            }
//
//            log.info("Returning existing Firebase App instance");
//            return FirebaseApp.getInstance();
//
//        } catch (IOException e) {
//            log.error("Error initializing Firebase App", e);
//            return FirebaseApp.getInstance();
//        }
//    }
//
//    @Bean
//    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
//        log.info("Creating FirebaseAuth instance");
//        return FirebaseAuth.getInstance(firebaseApp);
//    }
//}
