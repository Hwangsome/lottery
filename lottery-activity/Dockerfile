FROM docker.m.daocloud.io/library/eclipse-temurin:17-jre

ENV TZ=Asia/Shanghai
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Shanghai"
WORKDIR /app

COPY lottery-activity/target/lottery-activity-1.0.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
