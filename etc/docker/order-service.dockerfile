FROM eclipse-temurin:25-jre-alpine AS builder
WORKDIR /builder

ARG JAR_FILE=petstore-services/petstore-order-service/target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /application

RUN addgroup -S spring && adduser -S -G spring spring

COPY --from=builder --chown=spring:spring /builder/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /builder/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /builder/extracted/application/ ./

USER spring:spring
EXPOSE 8080 9090

ENTRYPOINT ["java", "-jar", "application.jar"]
