FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/policy-service/target/policy-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
