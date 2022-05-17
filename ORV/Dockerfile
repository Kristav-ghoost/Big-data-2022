FROM python:3.9-slim

MAINTAINER Projekt-Microsoft

WORKDIR /app 

RUN apt update

RUN apt install ffmpeg libsm6 libxext6  -y

RUN pip install Pillow

RUN pip install numpy

RUN pip install opencv-contrib-python

COPY ./docker-entrypoint.sh ./

RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["sh", "./docker-entrypoint.sh"]

CMD ["param1"]
