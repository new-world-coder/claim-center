FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/customer-service/target/customer-service-1.0.0.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
