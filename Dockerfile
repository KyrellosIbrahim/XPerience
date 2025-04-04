# Use the correct JDK base image for building Java applications
FROM eclipse-temurin:21-jdk-jammy

# Copy the JAR file built by Maven into the container
COPY target/XPS.jar /XPS.jar

# Copy the passwords file
COPY passwords.txt /passwords.txt

# Set the entry point for the container
# Passing port 8000 and the password file as arguments
ENTRYPOINT ["java", "-jar", "/XPS.jar", "8000", "/passwords.txt"]

# Expose the port your application will run on
EXPOSE 8000