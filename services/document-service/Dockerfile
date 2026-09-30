FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/document-service/target/document-service-1.0.0.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
