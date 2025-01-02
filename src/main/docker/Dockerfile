FROM openjdk:21

COPY ./OKR-System-4.0.jar /root/app.jar

EXPOSE 1701
EXPOSE 9999

ENV TZ=Asia/Shanghai

# RUN yum install fontconfig
WORKDIR /root
ENTRYPOINT java -jar /root/app.jar
