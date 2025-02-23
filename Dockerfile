#FROM openjdk:17-jdk-slim
#
#WORKDIR /app
#
#COPY target/cloud-file-storage-0.0.1-SNAPSHOT.jar app.jar
#COPY src/main/resources/application.yml /app/config/application.yml
#
#ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application.yml"]