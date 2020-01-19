FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD ./build/libs/funcorp-challenge-service-0.0.1-SNAPSHOT.jar myapp.jar
ADD crawlers.json crawlers.json
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/myapp.jar"]
EXPOSE 8080/tcp
ENV REDIS_HOST=localhost
ENV REDIS_PORT=6379
ENV SERVER_PORT=8080 
ENV CREATE_CRAWLERS=true
ENV CRAWLERS_CONFIG_FILE=crawlers.json
ENV SEDA_QUEUE_SIZE=10000
ENV LOCAL_STORAGE_PATH=.

