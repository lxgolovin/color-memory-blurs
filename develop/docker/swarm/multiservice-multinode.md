# Creating A Multi-Service Multi-Node Web App

Here is a basic diagram of how the 5 services will work:
![diagram](./img/architecture.png)

- a `backend` and `frontend` overlay network are needed. Nothing different about them other then that backend will help protect database from the voting web app. (similar to how a VLAN setup might be in traditional architecture)
- The database server should use a named volume for preserving data. Use the new `--mount` format to do this: `--mount type=volume,source=db-data,target=/var/lib/postgresql/data`

# Services (names below should be service names)
- vote
    - bretfisher/examplevotingapp_vote
    - web front end for users to vote dog/cat
    - ideally published on TCP 80. Container listens on 80
    - on frontend network
    - 2+ replicas of this container

- redis
    - redis:3.2
    - key/value storage for incoming votes
    - no public ports
    - on frontend network
    - 1 replica NOTE VIDEO SAYS TWO BUT ONLY ONE NEEDED

- worker
    - bretfisher/examplevotingapp_worker:java
    - backend processor of redis and storing results in postgres
    - no public ports
    - on frontend and backend networks
    - 1 replica

- db
    - postgres:9.4
    - one named volume needed, pointing to /var/lib/postgresql/data
    - on backend network
    - 1 replica

- result
    - bretfisher/examplevotingapp_result
    - web app that shows results
    - runs on high port since just for admins (lets imagine)
    - so run on a high port of your choosing (I choose 5001), container listens on 80
    - on backend network
    - 1 replica

# Solution:

Start Swarm
```bash
# On master launch
$ docker swarm init  --advertise-addr 192.168.0.28
Swarm initialized: current node (rw8ybwqus12eunc6p0aq6ib6m) is now a manager.

To add a worker to this swarm, run the following command:

    docker swarm join --token SWMTKN-1-662fyiqdabz3hz9sm6k4tbbkdn8l8otr2qsi3mnib8pn0g6chs-b55913rt3nxfdmaujdckh0lv7 192.168.0.28:2377

To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.
```
```bash
# On slave node
$ docker swarm join --token SWMTKN-1-662fyiqdabz3hz9sm6k4tbbkdn8l8otr2qsi3mnib8pn0g6chs-b55913rt3nxfdmaujdckh0lv7 192.168.0.28:2377
This node joined a swarm as a worker.
```

Now need to create networks:
```bash
$ docker network create -d overlay backend
$ docker network create -d overlay frontend
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
8jjiop7n6da1        backend             overlay             swarm
be5ec8022855        bridge              bridge              local
d88626408b15        docker_gwbridge     bridge              local
ddfxo7l83zko        frontend            overlay             swarm
538e90c9a078        host                host                local
w065m4s1p34y        ingress             overlay             swarm
ebad9eac464a        none                null                local
```

Start the services one by one.
```bash
$ docker service create --name vote -p 80:80 --network frontend --replicas 2 bretfisher/examplevotingapp_vote
$ docker service ps vote
ID                  NAME                IMAGE                                     NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
t2uhfg1lsr39        vote.1              bretfisher/examplevotingapp_vote:latest   node1               Running             Running 38 seconds ago                       
ksa7axa5rg1y        vote.2              bretfisher/examplevotingapp_vote:latest   node3               Running             Running 36 seconds ago  
```

Redis:
```bash
$ docker service create --name redis --network frontend redis:3.2
$ docker service ps redis
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
rskzvb3shga2        redis.1             redis:3.2           node2               Running             Running 25 seconds ago                       

$ docker service create --name db --network backend --mount type=volume,source=db-data,target=/var/lib/postgresql/data postgres:11.4
$ docker service ps db
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
jp6jm290hnuu        db.1                postgres:11.4       node1               Running             Running 24 seconds ago                     
```

The worker:
```bash
$ docker service create --name worker --network frontend --network backend bretfisher/examplevotingapp_worker:java
$ docker service ps worker
ID                  NAME                IMAGE                                     NODE                DESIRED STATE       CURRENT STATE                ERROR               PORTS
qhd6e2o75h7c        worker.1            bretfisher/examplevotingapp_worker:java   node2               Running             Running about a minute ago
```

Front
```bash
$ docker service create --name result --network backend -p 5001:80 bretfisher/examplevotingapp_result
$ docker service ps result
ID                  NAME                IMAGE                                       NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
wlbywzo6gdco        result.1            bretfisher/examplevotingapp_result:latest   node3               Running             Running 37 seconds ago
```

Checkout the nodes
```bash
$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE                                       PORTS
rrm7y6i2gxhm        db                  replicated          1/1                 postgres:11.4                               
paqzte2y9307        redis               replicated          1/1                 redis:3.2                                   
lid3pu0c2szo        result              replicated          1/1                 bretfisher/examplevotingapp_result:latest   *:5001->80/tcp
qmycyi1vjl34        vote                replicated          2/2                 bretfisher/examplevotingapp_vote:latest     *:80->80/tcp
ixfzx00x3jsa        worker              replicated          1/1                 bretfisher/examplevotingapp_worker:java     
```
