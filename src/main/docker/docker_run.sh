
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
-v /root/okr/media:/root/media \
-v /root/okr/logs:/root/logs \
-p 1701:1701 \
-p 9999:9999 \
-d ${OKR_IMAGE_NAME}
