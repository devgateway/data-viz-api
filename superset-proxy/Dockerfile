FROM maven:3-eclipse-temurin-21 AS base

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

FROM eclipse-temurin:21-jre-alpine
LABEL org.opencontainers.image.description="Superset Proxy image for Data Viz"
LABEL org.opencontainers.image.authors="Development Gateway <info@developmentgateway.org>"
LABEL org.opencontainers.image.url="https://github.com/devgateway/data-viz-api/tree/main/superset-proxy"
LABEL org.opencontainers.image.source="https://github.com/devgateway/data-viz-api"
LABEL org.opencontainers.image.vendor="Development Gateway"
LABEL org.opencontainers.image.licenses="Apache-2.0"

RUN apk add --no-cache bash
WORKDIR /opt/viz
COPY --from=compiler /opt/viz ./
COPY entrypoint.sh /opt/viz/
RUN chmod +x /opt/viz/entrypoint.sh
EXPOSE 8090
ENTRYPOINT ["/opt/viz/entrypoint.sh"]
