# Stack basic

The same [example](../swarm/multiservice-multinode.md), but using Stack tecnology of docker. There is only one file `stack.yml` to be created and used. Listing of the file could be found in the bottom.

## Start stack

Start it with setting compose file
```bash
$ docker stack deploy -c stack.yml voteapp
Creating network voteapp_default
Creating network voteapp_frontend
Creating network voteapp_backend
Creating service voteapp_visualizer
Creating service voteapp_redis
Creating service voteapp_db
Creating service voteapp_vote
Creating service voteapp_result
Creating service voteapp_worker
```

After some time processes will be up
```bash
$ docker service ls
ID                  NAME                 MODE                REPLICAS            IMAGE                                       PORTS
oiftfr6jlh9z        voteapp_db           replicated          1/1                 postgres:9.4                                
p884vqxof30q        voteapp_redis        replicated          1/1                 redis:alpine                                *:30002->6379/tcp
8d6n4bdu06kp        voteapp_result       replicated          1/1                 bretfisher/examplevotingapp_result:latest   *:5001->80/tcp
apawpd2c53cz        voteapp_visualizer   replicated          1/1                 dockersamples/visualizer:latest             *:8080->8080/tcp
03uhwe2e50i6        voteapp_vote         replicated          2/2                 bretfisher/examplevotingapp_vote:latest     *:5000->80/tcp
cq8cwww8npwz        voteapp_worker       replicated          1/1                 bretfisher/examplevotingapp_worker:java     
```

Services visualizer
![Services could be monitored by visualiser](./img/stack-monitor.png)

## Stack listings

```bash
$ docker stack ls
NAME                SERVICES            ORCHESTRATOR
voteapp             6                   Swarm

$ docker stack ps voteapp
ID                  NAME                   IMAGE                                       NODE                DESIRED STATE       CURRENT STATE                     ERROR               PORTS
y0kd79gkpco2        voteapp_vote.1         bretfisher/examplevotingapp_vote:latest     node3               Running             Starting 13 seconds ago                               
kxwof9m68732        voteapp_worker.1       bretfisher/examplevotingapp_worker:java     node1               Running             Starting 13 seconds ago                               
t7240zx6ke3n        voteapp_db.1           postgres:9.4                                node1               Running             Starting 18 seconds ago                               
ljifq3hw582n        voteapp_visualizer.1   dockersamples/visualizer:latest             node1               Running             Starting 18 seconds ago                               
4zqtgtqxe3mn        voteapp_result.1       bretfisher/examplevotingapp_result:latest   node2               Running             Preparing 23 seconds ago                              
ioe39jbhs73j        voteapp_redis.1        redis:alpine                                node2               Running             Starting 22 seconds ago                               
5q5g7ltpglbk        voteapp_vote.2         bretfisher/examplevotingapp_vote:latest     node2               Running             Starting 22 seconds ago                               
```

or
```bash
$ docker stack services voteapp
ID                  NAME                 MODE                REPLICAS            IMAGE                                       PORTS
03uhwe2e50i6        voteapp_vote         replicated          2/2                 bretfisher/examplevotingapp_vote:latest     *:5000->80/tcp
8d6n4bdu06kp        voteapp_result       replicated          1/1                 bretfisher/examplevotingapp_result:latest   *:5001->80/tcp
apawpd2c53cz        voteapp_visualizer   replicated          1/1                 dockersamples/visualizer:latest             *:8080->8080/tcp
cq8cwww8npwz        voteapp_worker       replicated          1/1                 bretfisher/examplevotingapp_worker:java     
oiftfr6jlh9z        voteapp_db           replicated          1/1                 postgres:9.4                                
p884vqxof30q        voteapp_redis        replicated          1/1                 redis:alpine                                *:30002->6379/tcp
```

And the networks:
```bash
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
8jjiop7n6da1        backend             overlay             swarm
be5ec8022855        bridge              bridge              local
d88626408b15        docker_gwbridge     bridge              local
ddfxo7l83zko        frontend            overlay             swarm
538e90c9a078        host                host                local
w065m4s1p34y        ingress             overlay             swarm
ebad9eac464a        none                null                local
6wy9p7fh6ji1        voteapp_backend     overlay             swarm
z6rh4y9p2wck        voteapp_default     overlay             swarm
6trbj3kvr67q        voteapp_frontend    overlay             swarm
```

## Stack updating

To update - correct stack file and launch deploy again:
```bash
$ docker stack deploy -c stack.yml voteapp
Updating service voteapp_visualizer (id: apawpd2c53czroktjoq1ff2fn)
Updating service voteapp_redis (id: p884vqxof30qs770u9ys0nn2g)
Updating service voteapp_db (id: oiftfr6jlh9z43yp4eenz5lp5)
Updating service voteapp_vote (id: 03uhwe2e50i6h3h6k0hfrg6ge)
Updating service voteapp_result (id: 8d6n4bdu06kp4v5tp0wp3st9l)
Updating service voteapp_worker (id: cq8cwww8npwz8tyrstx9ncysl)
```

## Listing of yaml config

```yaml
version: "3"
services:

  redis:
    image: redis:alpine
    ports:
      - "6379"
    networks:
      - frontend
    deploy:
      replicas: 1
      update_config:
        parallelism: 2
        delay: 10s
      restart_policy:
        condition: on-failure
  db:
    image: postgres:9.4
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - backend
    environment:
      - POSTGRES_HOST_AUTH_METHOD=trust
    deploy:
      placement:
        constraints: [node.role == manager]
  vote:
    image: bretfisher/examplevotingapp_vote
    ports:
      - 5000:80
    networks:
      - frontend
    depends_on:
      - redis
    deploy:
      replicas: 2
      update_config:
        parallelism: 2
      restart_policy:
        condition: on-failure
  result:
    image: bretfisher/examplevotingapp_result
    ports:
      - 5001:80
    networks:
      - backend
    depends_on:
      - db
    deploy:
      replicas: 1
      update_config:
        parallelism: 2
        delay: 10s
      restart_policy:
        condition: on-failure

  worker:
    image: bretfisher/examplevotingapp_worker:java
    networks:
      - frontend
      - backend
    depends_on:
      - db
      - redis
    deploy:
      mode: replicated
      replicas: 1
      labels: [APP=VOTING]
      restart_policy:
        condition: on-failure
        delay: 10s
        max_attempts: 3
        window: 120s
      placement:
        constraints: [node.role == manager]

  visualizer:
    image: dockersamples/visualizer
    ports:
      - "8080:8080"
    stop_grace_period: 1m30s
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock"
    deploy:
      placement:
        constraints: [node.role == manager]

networks:
  frontend:
  backend:

volumes:
  db-data:
```

## Architecture

![App architecture](../swarm/img/architecture.png)
