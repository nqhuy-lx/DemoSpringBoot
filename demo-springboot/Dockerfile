FROM eclipse-temurin:17-jdk

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} api-service.jar

ENTRYPOINT ["java","-jar","api-service.jar"]

EXPOSE 8080