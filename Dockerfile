
FROM albertoclarit/hisd3base:1.0.3

ARG PROJ_VERSION
VOLUME /tmp
WORKDIR /app
ADD build/libs/hismk2-$PROJ_VERSION.jar /app/HISD3.jar
ADD fonts  /usr/java/jdk1.8.0_181-amd64/jre/lib/fonts
RUN chmod a+w /app

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Djava.awt.headless=true","-jar","/app/HISD3.jar"]
