FROM maven:3.9.8-amazoncorretto-17-al2023 AS build

WORKDIR /app

COPY . /app

RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar entregaApp.jar

EXPOSE 8083

CMD ["java", "-jar", "entregaApp.jar"]