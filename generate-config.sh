#!/bin/bash
for i in {1..9}; do
  port=$((7000 + i))
  bus_port=$((17000 + i))

  cat > redis-node-$i.conf << EOF
port $port
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
cluster-announce-ip 127.0.0.1
cluster-announce-port $port
cluster-announce-bus-port $bus_port
requirepass redis
EOF
done