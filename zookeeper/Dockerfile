# syntax=docker/dockerfile:1

FROM confluentinc/cp-zookeeper:7.6.1

# Environment variables can be overridden by compose
ENV ZOOKEEPER_CLIENT_PORT=2181 \
    ZOOKEEPER_TICK_TIME=2000 \
    ZOOKEEPER_SYNC_LIMIT=2

EXPOSE 2181 2888 3888

