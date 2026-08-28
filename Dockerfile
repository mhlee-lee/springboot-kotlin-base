FROM harbor-cicd.ktcloud.com/kt-cloud-base/openjdk:temurin-25.36-a11

ENV JAVA_OPTIONS=""
ENV JVM_OPTIONS="-XshowSettings:vm -XX:MaxRAMPercentage=60.0"

ENV LOG_PATH=/logs/apps

USER root
RUN mkdir -p ${LOG_PATH}
RUN chown -R 1000:1000 /logs
USER 1000

COPY ./build/libs/*.jar app.jar

ENTRYPOINT ["sh","-c","umask 0027 && exec java $JAVA_OPTS $JAVA_OPTIONS $JVM_OPTIONS \
 -Djava.security.egd=file:/dev/./urandom \
 -Xlog:gc*:file=${LOG_PATH}/gc.log:time,level,uptime,tags:filecount=7,filesize=10M \
 -XX:ErrorFile=${LOG_PATH}/hs_err_pid%p.log \
 -jar app.jar"]
