FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw -Dmaven.repo.local=/root/.m2/repository -Dmaven.test.skip=true dependency:go-offline

COPY src/ src/

RUN ./mvnw -Dmaven.repo.local=/root/.m2/repository -Dmaven.test.skip=true package


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
