FROM openjdk:21

RUN mkdir /root/okr

COPY ./OKR-System-4.0.jar /root/okr/app.jar

EXPOSE 1701
EXPOSE 9999

ENV TZ=Asia/Shanghai

# 如果 java 字体有问题，构建镜像时需要下载 👇（因为有些 jdk 这方面有点问题）
# RUN yum install fontconfig -y
WORKDIR /root/okr

# S1:S2:E = 1:1:6，适当降低了 Eden 区的大小
# 设置一个虚拟机栈初始化 256k
# 使用 G1 回收器，并让老年代的字符串对象去重，进行共享
ENTRYPOINT java -jar -XX:+UseG1GC -XX:+UseStringDeduplication -XX:SurvivorRatio=6 -Xss256K /root/okr/app.jar
