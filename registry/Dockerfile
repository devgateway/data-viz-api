FROM maven:3-amazoncorretto-21 AS compiler
WORKDIR /tmp/build
COPY ./pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/tcdi \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/tcdi \
  && rm -rf *

FROM openjdk:21-jdk-slim
WORKDIR /opt/tcdi
COPY --from=compiler /opt/tcdi ./
RUN apt-get update && apt-get install -y \
curl
EXPOSE 8761
CMD ["java","-jar","/opt/tcdi/registry-0.0.1-SNAPSHOT.jar"]
