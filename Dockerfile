FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app

# Install necessary packages
RUN apk add --no-cache maven

# Copy pom.xml first for dependency resolution
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]