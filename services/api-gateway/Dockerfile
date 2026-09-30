FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/api-gateway/target/api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
