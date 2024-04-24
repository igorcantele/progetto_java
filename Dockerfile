FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /usr/src/app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

FROM openjdk:17-jdk-alpine

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/*.jar /usr/src/app/app.jar

CMD ["java","-jar","app.jar"]
