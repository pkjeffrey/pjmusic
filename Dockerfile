FROM openjdk:8-alpine

COPY target/uberjar/pjmusic.jar /pjmusic/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/pjmusic/app.jar"]
