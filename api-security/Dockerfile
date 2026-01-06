FROM maven:3-amazoncorretto-21 AS base

ARG PROJECT_NAME=api-security
ARG VERSION
ARG GITHUB_REPO

LABEL maintainer="Development Gateway"
LABEL version=${VERSION}
LABEL description="API Security for the Data Viz project"
LABEL github_repo=${GITHUB_REPO}

# Create Maven settings.xml
RUN mkdir -p /root/.m2 && \
    cat > /root/.m2/settings.xml << 'EOF'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>central</id>
      <name>Maven Central</name>
      <url>https://repo.maven.apache.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>

</settings>
EOF

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

FROM eclipse-temurin:21-jdk-jammy AS api-security
WORKDIR /opt/viz
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /opt/viz ./
EXPOSE 8763
CMD ["java","-jar","/opt/viz/api-security-0.0.1-SNAPSHOT.jar"]
