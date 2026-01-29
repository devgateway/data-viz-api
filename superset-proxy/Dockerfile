FROM maven:3-amazoncorretto-21 AS compiler
WORKDIR /tmp/build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/viz \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/viz \
  && rm -rf *

FROM eclipse-temurin:21-jdk-alpine AS runtime
RUN apk add --no-cache bash curl
WORKDIR /opt/viz
COPY --from=compiler /opt/viz ./

COPY entrypoint.sh /opt/viz/

ENTRYPOINT ["/opt/viz/entrypoint.sh"]