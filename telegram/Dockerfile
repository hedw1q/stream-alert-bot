FROM eclipse-temurin:17-jdk-jammy
MAINTAINER ryamovainur
VOLUME /tmp
ADD "target/telegram-0.0.1-SNAPSHOT.jar" telegram.jar
ENV JAVA_OPTS=""
EXPOSE 8081
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /telegram.jar" ]