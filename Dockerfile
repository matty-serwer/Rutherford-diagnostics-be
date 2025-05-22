FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY rutherford-d1/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"] 