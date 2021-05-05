FROM ubuntu:18.04
MAINTAINER RankCityTeam
RUN apt-get update
RUN apt-get install -y python3 python3-dev python3-pip
RUN apt install -y netcat
COPY . /app
WORKDIR /app
RUN pip3 install -r requirements.txt
ENV LC_ALL=C.UTF-8 LANG=C.UTF-8 FLASK_APP=app.py
COPY ./entrypoint.sh /app/
RUN chmod 777 entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]