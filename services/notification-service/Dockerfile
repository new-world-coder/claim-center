FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/notification-service/target/notification-service-1.0.0.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
