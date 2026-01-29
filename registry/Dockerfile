FROM maven:3-eclipse-temurin-11 AS compiler
WORKDIR /tmp/build
COPY ./pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/tcdi \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/tcdi \
  && rm -rf *

FROM eclipse-temurin:11-jdk 
WORKDIR /opt/tcdi
COPY --from=compiler /opt/tcdi ./
EXPOSE 8761
CMD ["java","-jar","/opt/tcdi/registry-0.0.1-SNAPSHOT.jar"]
