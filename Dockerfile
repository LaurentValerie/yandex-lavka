FROM openjdk:17.0.1-jdk-slim

WORKDIR /opt/app

# Копирование всех файлов в контейнер, включая build.gradle и gradlew
COPY . .

# Запуск сборки приложения с помощью Gradle Wrapper
RUN ./gradlew build

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

#COPY ${JAR_FILE} yandex-lavka.jar

RUN cp ${JAR_FILE} yandex-lavka.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","yandex-lavka.jar"]

# Запускаем приложение с параметром -Dserver.port=8080
#CMD ["java", "-Dserver.port=8080", "-jar", "yandex-lavka.jar"]