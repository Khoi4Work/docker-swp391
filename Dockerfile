# ==============================
# 🧱 Stage 1: Build ứng dụng bằng Maven
# ==============================
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy toàn bộ source code (bao gồm pom.xml, src, v.v.)
COPY . .

# Build project, bỏ qua test để nhanh hơn
RUN mvn clean package -DskipTests

# ==============================
# 🚀 Stage 2: Chạy ứng dụng
# ==============================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy file jar đã build từ stage trước
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 để Spring Boot lắng nghe
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
