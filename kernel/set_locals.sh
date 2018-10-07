#!/usr/bin/env bash
echo "Set local variables before start Worker"
#echo "don't forget this command: chmod +x set_locals.sh"

export AKKA_SEED_NODES='localhost:2551,localhost:2552'
echo "AKKA_SEED_NODES=$AKKA_SEED_NODES"

export AKKA_REMOTING_BIND_HOST='localhost'
echo "AKKA_REMOTING_BIND_HOST=$AKKA_REMOTING_BIND_HOST"

export AKKA_REMOTING_BIND_PORT='2551'
echo "AKKA_REMOTING_BIND_PORT=$AKKA_REMOTING_BIND_PORT"

export AKKA_ACTOR_SYSTEM_NAME='FileIOClusterSystem'
echo "AKKA_ACTOR_SYSTEM_NAME=$AKKA_ACTOR_SYSTEM_NAME"