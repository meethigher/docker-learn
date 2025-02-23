# 第一阶段：构建阶段
FROM maven:3.6.3-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段。将第一阶段的构建产物复制到第二阶段中
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/docker-learn.jar app.jar
CMD ["java", "-jar", "app.jar"]