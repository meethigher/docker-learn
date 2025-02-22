# 使用 Java 8 运行时环境 docker pull eclipse-temurin:8-jre-alpine
FROM eclipse-temurin:8-jre-alpine

# 设置容器中的工作目录
WORKDIR /docker-learn

# 复制 JAR 文件到设置的容器目录中
COPY target/docker-learn.jar app.jar

# 运行 Java 应用
CMD ["java", "-jar", "app.jar"]
