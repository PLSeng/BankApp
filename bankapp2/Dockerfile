# Use Maven with OpenJDK 21 as the build image
FROM maven:3.8.5-openjdk-21 as BUILD

# Copy the project files into the container
COPY . .

# Build the project with Java 21
RUN mvn clean package -DskipTests

# Use OpenJDK 21 as the runtime image
FROM adoptopenjdk:21-jre-hotspot

# Copy the JAR file built in the previous stage
COPY --from=BUILD /target/bankapp2-0.0.1-SNAPSHOT.jar bankapp2.jar

# Expose the port that your Java application listens on
EXPOSE 8080

# Define the entry point for the Java application
ENTRYPOINT ["java", "-jar", "bankapp2.jar"]

