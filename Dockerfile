FROM eclipse-temurin:21-jdk-jammy

COPY target/XPS.jar /XPS.jar
COPY passwords.txt /passwords.txt

# Add health check
HEALTHCHECK --interval=5s --timeout=3s \
  CMD curl -f http://localhost:8000 || exit 1

# Run in foreground
ENTRYPOINT ["java", "-jar", "/XPS.jar", "8000", "/passwords.txt"]
EXPOSE 8000
