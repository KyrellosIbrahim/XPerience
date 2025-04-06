FROM eclipse-temurin:21-jdk-jammy

# Add debugging tools
RUN apt-get update && apt-get install -y tree

# Copy and verify
COPY target/XPS.jar /XPS.jar
COPY passwords.txt /passwords.txt

# Debug step - list JAR contents
RUN jar tf /XPS.jar

ENTRYPOINT ["java", "-jar", "/XPS.jar", "8000", "/passwords.txt"]
