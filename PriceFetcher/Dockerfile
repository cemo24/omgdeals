FROM openjdk:11-jre-slim

WORKDIR /app

COPY build/libs/*.jar pricefetcher.jar

CMD ["java", "-jar", "pricefetcher.jar"]