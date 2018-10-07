# Run from sbt
$ . set_locals.sh
$ sbt \
  -Dakka.actor.provider=cluster \
  -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" \
  -Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo " \
  -Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done) \
  -DactorSystemName=${AKKA_ACTOR_SYSTEM_NAME} \
  run


# Run from sbt with debug
$ . set_locals.sh
$ sbt \
  -jvm-debug 5005 \
  -Dakka.actor.provider=cluster \
  -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" \
  -Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo " \
  -Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done) \
  -DactorSystemName=${AKKA_ACTOR_SYSTEM_NAME} \
  run
  

# Run in docker
# get image with openjdk:8-jre-alpine (in case if we didn't this step before)
cat <<EOF | docker build -t local/openjdk-jre-8-bash:latest -
FROM openjdk:8-jre-alpine
RUN apk --no-cache add --update bash coreutils curl
EOF

# create docker image
$ . set_locals.sh
$  sbt docker:publishLocal
# run image
$ . set_locals.sh
$  docker run \
    --env TZ=Europe/Moscow \
    --env AKKA_REMOTING_BIND_HOST \
    --env AKKA_REMOTING_BIND_PORT \
    --env AKKA_SEED_NODES \
    --env AKKA_ACTOR_SYSTEM_NAME \
    --env CASSANDRA_ADDRESS \
    --env CASSANDRA_PORT \
    --env CASSANDRA_KEYSPACE \
    --env CASSANDRA_LOGIN \
    --env CASSANDRA_PASSWORD \
    --name utils \
    --net=host \
    -d \
    utils:latest
or
$  docker run \
    --env TZ=Europe/Moscow \
    --env AKKA_REMOTING_BIND_HOST \
    --env AKKA_REMOTING_BIND_PORT \
    --env AKKA_SEED_NODES \
    --env AKKA_ACTOR_SYSTEM_NAME \
    --env CASSANDRA_ADDRESS \
    --env CASSANDRA_PORT \
    --env CASSANDRA_KEYSPACE \
    --env CASSANDRA_LOGIN \
    --env CASSANDRA_PASSWORD \
    --name utils \
    --net=host \
    utils:latest





# Cassandra (for local test)
$  docker run \
    -p 7000:7000 -p 7001:7001 -p 7199:7199 -p 9042:9042 -p 9160:9160 \
    --name cassy1 \
    -d \
    cassandra:3.11.0
