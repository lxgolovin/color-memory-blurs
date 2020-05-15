# DNS Round Robin test for containers

## Docker Networks: CLI Management

### 1. Pull Images

We'll create the example with `elasticseach` container.
```bash
$ docker pull elasticsearch:2

$ docker image ls
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
mysql               latest              a7a67c95e831        7 days ago          541MB
drupal              latest              b36ac715003a        11 days ago         454MB
ubuntu              latest              1d622ef86b13        11 days ago         73.9MB
postgres            latest              0f10374e5170        12 days ago         314MB
nginx               latest              602e111c06b6        12 days ago         127MB
elasticsearch       7.5.2               929d271f1798        3 months ago        779MB
elasticsearch       2                   5e9d896dc62c        20 months ago       479MB
```

### 2. Create new Network

Create network `dude` of type `bridge`
```bash
$ docker network create dude
647f7d5af644d433836f98d39763730d1820b93bbb940896ffe000d865041d27

$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
6b1dd7e8e55e        bridge              bridge              local
e5d21a0c2e64        docker_gwbridge     bridge              local
647f7d5af644        dude                bridge              local
248812af76cf        host                host                local
rstocox5g68a        ingress             overlay             swarm
75kmerk6tqbs        mydrupal            overlay             swarm
d6e24534dee9        new_network         bridge              local
4342d561491a        none                null                local
```

### 3. Create containers in the network

Create two containers in the network
```bash
$ docker container run -d --net dude --network-alias search elasticsearch:2
c686841935d83130fa4b177c005800cee24d7405360ec0b21584395b6fc2b458
$ docker container run -d --net dude --network-alias search elasticsearch:2
8483696618af32c8890dff8fa0b563e41ea95d19c5debd0d8f89f5ac73008594
```

Checkout:

```bash
$ docker container ls

CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                 NAMES
8483696618af        elasticsearch:2     "/docker-entrypoint.…"   7 minutes ago       Up 7 minutes        9200/tcp, 9300/tcp    cranky_pasteur
c686841935d8        elasticsearch:2     "/docker-entrypoint.…"   7 minutes ago       Up 7 minutes        9200/tcp, 9300/tcp    condescending_bell
b0d3565ec698        nginx               "nginx -g 'daemon of…"   About an hour ago   Up About an hour    80/tcp                new_nginx
8a06b5f5231b        mysql               "docker-entrypoint.s…"   4 hours ago         Up 4 hours          3306/tcp, 33060/tcp   mysql
a0e26dc95def        nginx               "nginx -g 'daemon of…"   4 hours ago         Up 4 hours          0.0.0.0:80->80/tcp    wonderful_tu
```

### 4. Check IPs for the elasticsearch Containers

```bash
$ docker container run --rm --network dude alpine nslookup search

Server:		127.0.0.11
Address:	127.0.0.11:53

Non-authoritative answer:

Non-authoritative answer:
Name:	search
Address: 172.19.0.2
Name:	search
Address: 172.19.0.3
```

### 5. Check containers in the network
```bash
$ docker network inspect --format '{{.Containers}}' dude

map[
    8483696618af32c8890dff8fa0b563e41ea95d19c5debd0d8f89f5ac73008594 : {
        cranky_pasteur
        eb60aacf5c0684d3468a6cbc06662a7320431a502ab947ae7f34a633607e0c42 02:42:ac:13:00:03 172.19.0.3/16
    }

    c686841935d83130fa4b177c005800cee24d7405360ec0b21584395b6fc2b458 : {
        condescending_bell
        b06823b7c350b56ccb1130d2840ad1c0bf14e7cbd2efe1237247ed87999acba8 02:42:ac:13:00:02 172.19.0.2/16
    }
]
```

### 6. Check DNS round robin

Check with curl by repeating several times. By the names one can see Round Robin switching
```bash
$ docker container run --rm --net dude centos curl -s search:9200
{
  "name" : "Blizzard II",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "uxmpP6jXTWC-D2Eg0R5jsg",
  "version" : {
    "number" : "2.4.6",
    "build_hash" : "5376dca9f70f3abef96a77f4bb22720ace8240fd",
    "build_timestamp" : "2017-07-18T12:17:44Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}

$ docker container run --rm --net dude centos curl -s search:9200
{
  "name" : "Fer-de-Lance",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "MGUKvq6SQ9-gyXxr2c0g2w",
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
