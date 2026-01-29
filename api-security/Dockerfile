FROM maven:3-amazoncorretto-21 AS compiler
WORKDIR /tmp/build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

COPY ./ ./

RUN mkdir /opt/tcdi \
  && mvn -B compile package -DskipTests \
  && mv ./target/*-0.0.1-SNAPSHOT.jar /opt/tcdi \
  && rm -rf *

FROM eclipse-temurin:21-jdk-alpine AS runtime 
RUN apk add --no-cache bash curl
WORKDIR /opt/tcdi
COPY --from=compiler /opt/tcdi ./
EXPOSE 8763
CMD ["java","-jar","/opt/tcdi/api-security-0.0.1-SNAPSHOT.jar"]
