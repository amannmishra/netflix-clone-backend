# Use Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy only Maven files first (for caching dependencies)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Install Maven and build dependencies
RUN apt-get update && apt-get install -y maven

# Download dependencies separately (speeds up rebuilds)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the project (skip tests)
RUN mvn clean package -DskipTests

# Run the jar
CMD ["sh", "-c", "java -jar target/*.jar"]