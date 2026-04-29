FROM maven:3-amazoncorretto-21 AS base

ARG PROJECT_NAME=api-security
ARG VERSION
ARG GITHUB_REPO

LABEL maintainer="Development Gateway"
LABEL version=${VERSION}
LABEL description="API Security for the Data Viz project"
LABEL github_repo=${GITHUB_REPO}


FROM base AS installer
WORKDIR /tmp/build
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -f pom.xml dependency:go-offline

FROM installer AS builder

COPY ./ ./

RUN --mount=type=cache,target=/root/.m2 mkdir /opt/viz \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/viz \
  && rm -rf *

FROM eclipse-temurin:21-jdk-slim AS api-security
WORKDIR /opt/viz
COPY --from=builder /opt/viz ./
EXPOSE 8763
CMD ["java","-jar","/opt/viz/api-security-0.0.1-SNAPSHOT.jar"]
