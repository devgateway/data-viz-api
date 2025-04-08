FROM maven:3-amazoncorretto-21 AS compiler
WORKDIR /tmp/build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/viz \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/viz \
  && rm -rf *

FROM openjdk:21-jdk-slim
WORKDIR /opt/viz
COPY --from=compiler /opt/viz ./
EXPOSE 8762
CMD ["java","-jar","/opt/viz/api-gateway-0.0.1-SNAPSHOT.jar"]
