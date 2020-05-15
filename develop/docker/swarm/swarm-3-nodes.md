# Swarm with 3 nodes

## Before all

Here are the ways to play with Swarm and nodes
* [Play with Docker](https://play-with-docker.com/) - only need a browser, but resets after 4 hours
* docker-machine + VirtualBox - this option is free and runs locally, but requires a machine with 8GB memory or so
    - `docker-machine create node1`
    - `docker-machine ssh node1`
* Digital Ocean + Docker install - most like production setup, but costs $5-20/node/month
* Roll your own - docker-machine can provision machines for Amason, Azure, DO, Google.

## Let's get this party started

To play, we can use nice tool (4 free hours) [Play with Docker](https://play-with-docker.com/)

### 1. Create 3 nodes with tool

```bash
$ docker swarm init  --advertise-addr 192.168.0.11

Swarm initialized: current node (fujhuq26xo3o3wmo6m2chalhc) is now a manager.
To add a worker to this swarm, run the following command:
    docker swarm join --token SWMTKN-1-1k8ef211xf7h1mwf49g2ro7w5e2zvvq5mm6ew71232z7ff6ki3-3v4lf9iierp30z787zvko9fit 192.168.0.11:2377
To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.
```
Same on all three nodes (IPs differ).
Now copy join token. If lost, try to restore token with the command:
```bash
$ docker swarm join-token manager

To add a manager to this swarm, run the following command:
    docker swarm join --token SWMTKN-1-1k8ef211xf7h1mwf49g2ro7w5e2zvvq5mm6ew71232z7ff6ki3-9gkkj5a8u8nszt6t9qvdgzvbt 192.168.0.11:2377
```

### 2. Join two other nodes

Join 2 nodes:
```bash
$ docker swarm join --token SWMTKN-1-1k8ef211xf7h1mwf49g2ro7w5e2zvvq5mm6ew71232z7ff6ki3-3v4lf9iierp30z787zvko9fit 192.168.0.11:2377
This node joined a swarm as a worker.
```

### 3. Check out all nodes

On the master node:
```bash
$ docker node ls

ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
2bnr9vpf3nz5ibpapon744a9w     node2               Ready               Active                                  19.03.4
fujhuq26xo3o3wmo6m2chalhc *   node3               Ready               Active              Leader              19.03.4
```

Again:

```bash
$ docker node ls

ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
ivk6qjywvo6tas1g6v6z2cl0y     node1               Ready               Active                                  19.03.4
2bnr9vpf3nz5ibpapon744a9w     node2               Ready               Active                                  19.03.4
fujhuq26xo3o3wmo6m2chalhc *   node3               Ready               Active              Leader              19.03.4
```

### 4. Start services

```bash
$ docker service create --replicas 3 alpine ping 8.8.8.8

y6vtawaw3wv8e0c0albj0lcek
overall progress: 3 out of 3 tasks
1/3: running   [==================================================>]
2/3: running   [==================================================>]
3/3: running   [==================================================>]
verify: Service converged
```
```bash
$ docker service ls

ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
y6vtawaw3wv8        amazing_chatelet    replicated          3/3                 alpine:latest
```

Check nodes (master node):

```bash
$ docker node ps

ID                  NAME                 IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
ygazj6kv4x4v        amazing_chatelet.1   alpine:latest       node3               Running             Running 54 seconds ago   
```

Check nodes (slave node):

```bash
$ docker node ps node2
ID                  NAME                 IMAGE               NODE                DESIRED STATE       CURRENT STATE                ERROR               PORTS
3aww7hk0p0iu        amazing_chatelet.2   alpine:latest       node2               Running             Running about a minute ago                       
```

Check nodes (slave node):

```bash
$ docker node ps node1

ID                  NAME                 IMAGE               NODE                DESIRED STATE       CURRENT STATE                ERROR               PORTS
ki6yiwoub9t5        amazing_chatelet.3   alpine:latest       node1               Running             Running about a minute ago                    
```

### 5. Check nodes

```bash
$ docker service ps amazing_chatelet

ID                  NAME                 IMAGE               NODE                DESIRED STATE       CURRENT STATE                ERROR               PORTS
ygazj6kv4x4v        amazing_chatelet.1   alpine:latest       node3               Running             Running about a minute ago                       
3aww7hk0p0iu        amazing_chatelet.2   alpine:latest       node2               Running             Running about a minute ago                       
ki6yiwoub9t5        amazing_chatelet.3   alpine:latest       node1               Running             Running about a minute ago   
```

### 6. Stop all

Same as in previous example:

```bash
$ docker service rm amazing_chatelet
amazing_chatelet

$ docker service ls

ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
[node3] (local) root@192.168.0.11 ~

$ docker node ls

ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
ivk6qjywvo6tas1g6v6z2cl0y     node1               Ready               Active                                  19.03.4
2bnr9vpf3nz5ibpapon744a9w     node2               Ready               Active                                  19.03.4
fujhuq26xo3o3wmo6m2chalhc *   node3               Ready               Active              Leader              19.03.4
```
