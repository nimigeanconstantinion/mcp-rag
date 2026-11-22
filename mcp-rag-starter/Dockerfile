# ---- build stage: Maven + JDK 17 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Dependencies first, so they stay cached while only the sources change
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests \
 && cp target/*.jar app.jar

# ---- runtime stage: JRE 17 on Ubuntu noble ----
FROM eclipse-temurin:17-jre-noble
WORKDIR /app

# Non-root user without a login shell
RUN groupadd --system app \
 && useradd --system --gid app --no-create-home --shell /usr/sbin/nologin app

COPY --from=build --chown=app:app /workspace/app.jar app.jar

USER app

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
EXPOSE 8082

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
