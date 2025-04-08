FROM maven:3-amazoncorretto-21 AS base

ARG PROJECT_NAME=api-gateway
ARG VERSION
ARG GITHUB_REPO

LABEL maintainer="Development Gateway"
LABEL version=${VERSION}
LABEL description="Superset Proxy for the Data Viz project"
LABEL github_repo=${GITHUB_REPO}


FROM base AS installer
WORKDIR /tmp/build
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -f pom.xml dependency:go-offline

FROM installer AS compiler

COPY ./ ./

RUN --mount=type=cache,target=/root/.m2 mkdir /opt/viz \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/viz \
  && rm -rf *


FROM openjdk:21-jdk-slim AS runtime
WORKDIR /opt/viz
COPY --from=compiler /opt/viz ./

COPY entrypoint.sh /opt/viz/

ENTRYPOINT ["/opt/viz/entrypoint.sh"]