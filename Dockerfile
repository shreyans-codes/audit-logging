# Use an OpenJDK image
FROM openjdk:17-jdk-alpine

# Create a directory for the application
# Ensure this directory matches where your application expects to write logs
RUN addgroup -S app && adduser -S dev -G app
RUN mkdir /app
RUN chown dev:app /app

# Set the user to use from now on
USER dev

# Set the working directory
WORKDIR /app

# Copy the application JAR file into the container
COPY --chown=dev:app target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]