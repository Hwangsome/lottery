# 基础镜像（国内镜像源）
FROM docker.m.daocloud.io/library/eclipse-temurin:17-jre

# 维护者信息
LABEL maintainer="Bill"

# 设置时区
ENV TZ=Asia/Shanghai
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Shanghai"

# 工作目录
WORKDIR /app

# 复制应用程序 JAR 文件
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
