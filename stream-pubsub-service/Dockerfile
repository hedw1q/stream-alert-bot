FROM eclipse-temurin:17-jdk-jammy
MAINTAINER ryamovainur
VOLUME /tmp
ADD "target/stream-service-0.0.1-SNAPSHOT.jar" stream-service.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /stream-service.jar" ]