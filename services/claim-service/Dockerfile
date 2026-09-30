FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/claim-service/target/claim-service-1.0.0.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
