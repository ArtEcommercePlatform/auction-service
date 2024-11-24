# Use an official OpenJDK image with Alpine 17
FROM openjdk:17-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/auction-service-0.0.1-SNAPSHOT.jar /app/auction-service.jar

# Expose the port your application runs on
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "auction-service.jar"]
