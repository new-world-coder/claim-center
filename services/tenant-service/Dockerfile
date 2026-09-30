FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/tenant-service/target/tenant-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
