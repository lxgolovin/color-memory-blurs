# Routing in swarm

## Swarm overlay multi-host networking

* Just choose `--driver overlay` when creating network
* For container-to-container traffic inside a single Swarm
* Optional IPSec (AES) encryption on network creation
* Each service can be connected to multiple networks (e.g. front-end, back-end)

## Swarm overlay example

### 1. Creating a network
Let's create a network first:
```bash
$ docker network create --driver overlay mydrupal
$ docker network ls

NETWORK ID          NAME                DRIVER              SCOPE
cb21ea2c730e        bridge              bridge              local
e5d21a0c2e64        docker_gwbridge     bridge              local
248812af76cf        host                host                local
rstocox5g68a        ingress             overlay             swarm
75kmerk6tqbs        mydrupal            overlay             swarm
4342d561491a        none                null                local
```

### 2. Starting service

Now let's create postgres service
```bash
$ docker service create --name psql --network mydrupal -e POSTGRES_PASSWORD=mypass postgres
lh4w98i3ck5454z9boaa9xs95
overall progress: 1 out of 1 tasks
1/1: running   [==================================================>]
verify: Service converged
```

Pay attention that the service is on node 1
```bash
$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
lh4w98i3ck54        psql                replicated          1/1                 postgres:latest     
```
```bash
$ docker service ps psql
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
z00xqkme1249        psql.1              postgres:latest     node1               Running             Running 27 seconds ago                       
```

And now drupal
```bash
$ docker service create --name drupal --network mydrupal -p 80:80 drupal
pggks6key5y2p76s3lbh8hiko
overall progress: 1 out of 1 tasks
1/1: running   [==================================================>]
verify: Service converged
```

And this service is on node 2.
```bash
$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
pggks6key5y2        drupal              replicated          1/1                 drupal:latest       *:80->80/tcp
lh4w98i3ck54        psql                replicated          1/1                 postgres:latest     
```
```bash
$ docker service ps drupal
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE           ERROR               PORTS
qqsnfamjurhc        drupal.1            drupal:latest       node2               Running             Running 4 minutes ago                   
```

Check out how do they communicate. From both nodes drupal is accessible. It appears as like drupal works on all nodes and with the same web page:
```bash
$ curl -I localhost # or the IP
HTTP/1.1 302 Found
Date: Wed, 29 Apr 2020 04:46:09 GMT
Server: Apache/2.4.25 (Debian)
X-Powered-By: PHP/7.3.17
Cache-Control: no-cache, private
Location: /core/install.php
Content-Type: text/html; charset=UTF-8
```
Inspect the service and check the IP
```bash
$ docker service inspect drupal
...
            "VirtualIPs": [
                {
                    "NetworkID": "76sdz5r0hwwhgzttckuxpett8",
                    "Addr": "10.255.0.4/16"
                },
                {
                    "NetworkID": "jiacf4s3jn0b8nthqutg5akxc",
                    "Addr": "10.0.0.5/24"
                }
            ]
...
```

## Swarm routing

This is a feature of load balancing with VirtualIPs, Routing Mesh
* Routes ingress (incoming) packets for a Service to proper Task
* Spans all nodes in Swarm
* Uses IPVS from Linux Kernel
* Load balances Swarm Services across their Tasks
* Two ways this works:
    - Container-to-container in a Overlay network (uses VIP)
    - External traffic incoming to published ports (all nodes listen)

![image][1] Container to container with Virtual IP

![image][2] External traffic incoming to published ports

## Swarm routing example

Create 3 replicas of elasticsearch:
```bash
$ docker service create --name search --replicas 3 -p 9200:9200 elasticsearch:2
artjasrwrl8mz9y1wyaky0vbe
overall progress: 3 out of 3 tasks
1/3: running   [==================================================>]
2/3: running   [==================================================>]
3/3: running   [==================================================>]
verify: Service converged
[node1] (local) root@192.168.0.8 ~
$ docker service ps
drupal  psql    search  
[node1] (local) root@192.168.0.8 ~
$ docker service ps
drupal  psql    search  
[node1] (local) root@192.168.0.8 ~
$ docker service ps search
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
defhxc6ybd3w        search.1            elasticsearch:2     node2               Running             Running 21 seconds ago                       
ihdzhnatghc3        search.2            elasticsearch:2     node2               Running             Running 21 seconds ago                       
v3ijr2ejagye        search.3            elasticsearch:2     node1               Running             Running 23 seconds ago                  
```

Now check to which elasticsearch to you connect, repeating the same command:

```bash
$ curl localhost:9200
{
  "name" : "Landslide",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "PIOztqExRXe97a59o-hFpA",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
$ curl localhost:9200
{
  "name" : "Freedom Ring",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "VVZ_fiffQ529pIarveFLjw",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
$ curl localhost:9200
{
  "name" : "Doctor Anthony Droom",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "JPUWsc0KSliMhmx4dsifzg",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
$ curl localhost:9200
{
  "name" : "Landslide",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "PIOztqExRXe97a59o-hFpA",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
$ curl localhost:9200
{
  "name" : "Freedom Ring",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "VVZ_fiffQ529pIarveFLjw",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
$ curl localhost:9200
{
  "name" : "Doctor Anthony Droom",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "JPUWsc0KSliMhmx4dsifzg",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
```

[1]: ./img/routing-mesh-inside.png
[2]: ./img/routing-mesh-ingress.png
