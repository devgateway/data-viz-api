FROM maven:3.6-jdk-11-slim AS compiler
WORKDIR /tmp/build
COPY api-common/commons/pom.xml api-common/commons/
COPY api-common/pom.xml api-common/pom.xml

RUN cd  api-common \
    && mvn -B -f pom.xml dependency:go-offline

COPY api-common api-common
RUN mkdir /opt/viz \
    && cd api-common \
    && mvn -B clean compile package install -DskipTests \
    && mv */target/*-0.0.1-SNAPSHOT.jar /opt/viz \
    && cd /..

COPY ./ services

RUN  cd services  \
      && mvn -B -f pom.xml dependency:go-offline

COPY ./ services
RUN  cd services \
    && mvn -B clean compile package -DskipTests \
    && mv -f */target/*.jar /opt/viz \
    && cd /..
COPY api-common/entrypoint.sh /opt/viz/

FROM openjdk:11-jdk-slim
RUN apt-get update && apt-get install -y curl
WORKDIR /opt/viz
COPY --from=compiler /opt/viz ./
RUN ls -l /opt/viz
COPY files ./
EXPOSE 8082 8761
ENTRYPOINT ["/opt/viz/entrypoint.sh"]
