FROM maven:3.10.0-openjdk-21 as builder
WORKDIR /workspace
COPY . /workspace/.
RUN mvn -f /workspace/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /workspace
COPY --from=builder /workspace/target/*.jar /workspace/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/workspace/*.jar"]