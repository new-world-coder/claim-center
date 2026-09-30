FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY services/audit-service/target/audit-service-1.0.0.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
