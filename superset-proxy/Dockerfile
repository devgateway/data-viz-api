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
COPY .mvn-settings.xml /root/.m2/settings.xml
RUN --mount=type=cache,target=/root/.m2 \
  find /root/.m2/repository -name "*.lastUpdated" -delete 2>/dev/null || true \
  && rm -rf /root/.m2/repository/org/springframework/cloud/spring-cloud-dependencies 2>/dev/null || true \
  && rm -rf /root/.m2/repository/org/springframework/boot/spring-boot-starter-parent 2>/dev/null || true \
  && for i in 1 2 3; do \
    mvn -B -U -s /root/.m2/settings.xml -f pom.xml dependency:go-offline && break || sleep 10; \
  done

FROM installer AS compiler

COPY ./ ./
RUN mkdir -p /root/.m2 \
    && (test -f .mvn-settings.xml && cp .mvn-settings.xml /root/.m2/settings.xml || \
        printf '<?xml version="1.0" encoding="UTF-8"?>\n<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 http://maven.apache.org/xsd/settings-1.2.0.xsd">\n  <mirrors>\n    <mirror>\n      <id>central-mirror</id>\n      <name>Maven Central Mirror</name>\n      <url>https://repo1.maven.org/maven2</url>\n      <mirrorOf>*</mirrorOf>\n    </mirror>\n  </mirrors>\n  <profiles>\n    <profile>\n      <id>default</id>\n      <repositories>\n        <repository>\n          <id>central</id>\n          <name>Maven Central</name>\n          <url>https://repo1.maven.org/maven2</url>\n          <releases><enabled>true</enabled><updatePolicy>daily</updatePolicy></releases>\n          <snapshots><enabled>false</enabled></snapshots>\n        </repository>\n      </repositories>\n      <pluginRepositories>\n        <pluginRepository>\n          <id>central</id>\n          <name>Maven Central</name>\n          <url>https://repo1.maven.org/maven2</url>\n          <releases><enabled>true</enabled><updatePolicy>daily</updatePolicy></releases>\n          <snapshots><enabled>false</enabled></snapshots>\n        </pluginRepository>\n      </pluginRepositories>\n    </profile>\n  </profiles>\n  <activeProfiles>\n    <activeProfile>default</activeProfile>\n  </activeProfiles>\n</settings>\n' > /root/.m2/settings.xml)

RUN --mount=type=cache,target=/root/.m2 mkdir /opt/viz \
  && find /root/.m2/repository -name "*.lastUpdated" -delete 2>/dev/null || true \
  && rm -rf /root/.m2/repository/org/springframework/cloud/spring-cloud-dependencies 2>/dev/null || true \
  && for i in 1 2 3; do \
    mvn -B -U -s /root/.m2/settings.xml compile package -DskipTests && break || sleep 10; \
  done \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/viz \
  && rm -rf *


FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /opt/viz
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=compiler /opt/viz ./

COPY entrypoint.sh /opt/viz/

EXPOSE 8090

ENTRYPOINT ["/opt/viz/entrypoint.sh"]