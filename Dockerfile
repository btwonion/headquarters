ARG BUILD_DIR="/usr/src/headquarters-server"

FROM gradle:7.6-jdk AS builder

ARG BUILD_DIR

WORKDIR $BUILD_DIR
COPY . .
RUN gradle :headquarters-server:installDist --no-daemon

FROM openjdk:17-slim

ARG BUILD_DIR

WORKDIR /app/
COPY --from=builder $BUILD_DIR/headquarters-server/build/install/headquarters-server/ .

CMD ["./bin/headquarters-server"]