FROM openjdk:21

COPY ./OKR-System-4.0.jar /root/app.jar

EXPOSE 1701
EXPOSE 9999

ENV TZ=Asia/Shanghai

# 如果 java 字体有问题，需要下载 👇
# RUN yum install fontconfig
WORKDIR /root
ENTRYPOINT java -jar /root/app.jar
