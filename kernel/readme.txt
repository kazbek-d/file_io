
# Run from sbt
$ . set_locals.sh
$ sbt \
  -Dakka.actor.provider=cluster \
  -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" \
  -Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo " \
  -Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done) \
  -DactorSystemName=${AKKA_ACTOR_SYSTEM_NAME} \
  run





# Run in docker
# get image with openjdk:8-jre-alpine
cat <<EOF | docker build -t local/openjdk-jre-8-bash:latest -
FROM openjdk:8-jre-alpine
RUN apk --no-cache add --update bash coreutils curl
EOF

# create docker image
$ . set_locals.sh
$ sbt docker:publishLocal
$ . set_locals.sh
# run image
$  docker run \
    --env TZ=Europe/Moscow \
    --env AKKA_REMOTING_BIND_HOST \
    --env AKKA_REMOTING_BIND_PORT \
    --env AKKA_SEED_NODES \
    --env AKKA_ACTOR_SYSTEM_NAME \
    --name kernel1 \
    --net=host \
    -d \
    kernel:latest
or
$  docker run \
    --env TZ=Europe/Moscow \
    --env AKKA_REMOTING_BIND_HOST \
    --env AKKA_REMOTING_BIND_PORT \
    --env AKKA_SEED_NODES \
    --env AKKA_ACTOR_SYSTEM_NAME \
    --name kernel1 \
    --net=host \
    kernel:latest
