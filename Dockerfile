# Use the correct JDK base image for building Java applications
FROM eclipse-temurin:21-jdk-jammy

# Copy the JAR file built by Maven into the container
COPY target/XPS.jar /XPS.jar

# Copy the passwords file
COPY passwords.txt /passwords.txt

# expose the port the server will be on
EXPOSE 8000
# Passing port 8000 and the password file as arguments
ENTRYPOINT ["java", "-jar", "/XPS.jar", "8000", "/passwords.txt"]

