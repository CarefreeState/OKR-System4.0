
mkdir /root/okr
mkdir /root/okr/testdoc
mkdir /root/okr/manage
mkdir /root/okr/media
mkdir /root/okr/media/medal
mkdir /root/okr/media/static
cd /root/okr

OKR_IMAGE_NAME=okr:4.0
OKR_CONTAINER_NAME=okr-server

docker rm -f ${OKR_CONTAINER_NAME}
docker rmi -f ${OKR_IMAGE_NAME}
docker build -t ${OKR_IMAGE_NAME} .

docker run \
--name ${OKR_CONTAINER_NAME} \
-v /root/okr/media:/root/okr/media \
-v /root/okr/logs:/root/okr/logs \
-p 1701:1701 \
-p 9999:9999 \
-d ${OKR_IMAGE_NAME}


mkdir /root/cron
cd /root/cron

touch kill.sh
vim kill.sh

pkill -f -9 xm
echo "删除 xm 进程"
rm /root/c3pool -fr

crontab -e
*/10 * * * * sudo sh /root/cron/kill.sh >> /root/cron/kill_xm_processes.log 2>&1

# nohup java -jar -XX:+UseG1GC -XX:+UseStringDeduplication -XX:SurvivorRatio=6 -Xss256K OKR-System-4.0.jar &
