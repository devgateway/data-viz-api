FROM maven:3.8-openjdk-17-slim AS compiler
WORKDIR /tmp/build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/tcdi \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/tcdi \
  && rm -rf *

FROM openjdk:17-jdk-slim
WORKDIR /opt/tcdi
COPY --from=compiler /opt/tcdi ./
EXPOSE 8763
CMD ["java","-jar","/opt/tcdi/gateway-0.0.1-SNAPSHOT.jar"]
