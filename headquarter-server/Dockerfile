ARG BUILD_DIR="/usr/src/headquartewr-server/"

FROM gradle:7.5.1-jdk AS builder

ARG BUILD_DIR

WORKDIR $BUILD_DIR
COPY .. .
RUN ./gradlew installDist --no-daemon --stacktrace

FROM openjdk:18-slim

ARG BUILD_DIR

WORKDIR /app/
COPY --from=builder $BUILD_DIR/headquarter-server/build/install/headquarter-server/ .

CMD ["./bin/headquarter-server"]