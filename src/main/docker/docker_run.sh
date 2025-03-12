
mkdir /root/okr
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

ds=$(ps -ef | grep '[x]m' | awk '{print $2}')

if [ -n "$pids" ]; then
    echo "Killing processes: $pids"
    kill -9 $pids
else
    echo "No processes found."
fi

crontab -e
0 * * * * /root/cron/kill.sh >> /root/cron/kill_xm_processes.log 2>&1

