FROM maven:3.8.5-openjdk-17 as BUILD
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=BUILD /target/bankapp2-0.0.1-SNAPSHOT.jar bankapp2.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bankapp2.jar"]

