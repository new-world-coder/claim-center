FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/auth-service/target/auth-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
