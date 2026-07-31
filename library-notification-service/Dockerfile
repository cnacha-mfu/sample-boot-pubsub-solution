# syntax=docker/dockerfile:1
#
# NOT USED by docker-compose.yml yet - kept here for reference. It is the same
# two-stage shape the lab uses: stage 1 builds the jar with Maven inside
# Docker, stage 2 keeps only the jar on a small Java runtime.
#
# One difference from the lab: this repo's modules share a parent pom, so the
# build needs the whole repo as its context. Build from the REPO ROOT:
#
#   docker build -f library-notification-service/Dockerfile -t library-notification-service .
#
# To run it against the compose broker, join the same network and use the
# Docker-side door (kafka:9092), not localhost:9094:
#
#   docker run --network sample-net -p 8201:8201 -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 library-notification-service

FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app
# The parent pom lists every module, so Maven needs all the module poms to
# read the build - but only THIS module's sources.
COPY pom.xml .
COPY library-borrow-service/pom.xml library-borrow-service/
COPY library-notification-service/pom.xml library-notification-service/
COPY library-popularity-service/pom.xml library-popularity-service/
COPY library-notification-service/src library-notification-service/src
RUN mvn -q -B -DskipTests -pl library-notification-service -am package

FROM eclipse-temurin:11-jre
WORKDIR /app
ENV JAVA_OPTS=""
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/library-notification-service/target/*.jar /app/app.jar
EXPOSE 8201
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
