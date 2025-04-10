# JAVA 17
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# JAR 파일을 컨테이너에 복사
COPY build/libs/tteoksang-backend.jar tteoksang-backend.jar

# 컨테이너에서 실행할 명령어
ENTRYPOINT ["java", "-jar", "tteoksang-backend.jar"]