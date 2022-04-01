FROM maven:3.8.2-openjdk-17 AS MAVEN_BUILD 
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -DskipTests=true package

FROM openjdk:17-jdk

WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/timetablemanager*.jar /app/timetablemanager.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "timetablemanager.jar"]