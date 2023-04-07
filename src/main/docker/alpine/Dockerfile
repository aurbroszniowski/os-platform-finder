FROM alpine:latest
RUN apk add openjdk8

WORKDIR /var/tmp/
ARG JAR_FILE=os-platform-finder-1.1.jar
COPY ${JAR_FILE} os-platform-finder.jar
ENTRYPOINT ["java","-jar","os-platform-finder.jar"]

