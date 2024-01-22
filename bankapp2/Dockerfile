# Use an official Java runtime as a parent image
FROM openjdk:21

# Set the working directory in the container
WORKDIR /usr/src/

RUN rm -rf /var/lib/apt/lists/*

# Copy the JAR file into the container at /usr/src/app
COPY target/bankapp2-0.0.1-SNAPSHOT.jar /usr/src/app

# Run the JAR file
ENTRYPOINT ["java", "-jar", "bankapp2-0.0.1-SNAPSHOT.jar"]
