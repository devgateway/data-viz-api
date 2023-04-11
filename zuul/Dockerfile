FROM maven:3.6-jdk-11-slim AS compiler
WORKDIR /tmp/build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/tcdi \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/tcdi \
  && rm -rf *

FROM openjdk:11-jdk-slim
WORKDIR /opt/tcdi
COPY --from=compiler /opt/tcdi ./
EXPOSE 8762
CMD ["java","-jar","/opt/tcdi/zuul-0.0.1-SNAPSHOT.jar"]
