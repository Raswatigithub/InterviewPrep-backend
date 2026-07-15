FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy project files and build
COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 5000
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
