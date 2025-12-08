FROM maven:3-eclipse-temurin-21 AS compiler
WORKDIR /tmp/build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/tcdi \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/tcdi \
  && rm -rf *

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/tcdi
COPY --from=compiler /opt/tcdi ./
EXPOSE 8762
CMD ["java","-jar","/opt/tcdi/zuul-0.0.1-SNAPSHOT.jar"]
