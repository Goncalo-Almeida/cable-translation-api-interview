FROM amazoncorretto:21-alpine-jdk
COPY target/translation-api-1.0-SNAPSHOT.jar translation-api.jar
ENTRYPOINT ["java","-jar","/translation-api.jar"]