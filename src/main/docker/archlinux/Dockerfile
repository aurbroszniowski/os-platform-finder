FROM archlinux:latest

USER root
RUN pacman -Syy -q --noconfirm jdk8-openjdk && printf "y\\ny\\n" | pacman -Scc

WORKDIR /var/tmp/
ARG JAR_FILE=os-platform-finder-1.1.jar
COPY ${JAR_FILE} os-platform-finder.jar
ENTRYPOINT ["java","-jar","os-platform-finder.jar"]

