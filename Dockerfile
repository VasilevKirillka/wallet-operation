FROM khipu/openjdk17-alpine
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar /app
EXPOSE 8090
ENTRYPOINT ["java","-jar","demo-0.0.1-SNAPSHOT.jar"]