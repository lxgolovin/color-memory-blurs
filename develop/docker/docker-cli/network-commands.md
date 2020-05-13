# Network commands

## Docker Networks: CLI Management

* Show networks `docker network ls`
* Inspect a network `docker network inspect`
* Create a network `docker network create --driver`
* Attach a network to container `docker network connect`
* Detach a network from container `docker network disconnect`

## IPs and Ports management

Check port usage
```bash
$ docker port wonderful_tu
80/tcp -> 0.0.0.0:80
```
> Docker port mapping
>
> Old style: `docker port proxy`
>
> New style: `docker container port proxy`

Check the IP of the container
```bash
$ docker container inspect --format '{{.NetworkSettings.IPAddress}}' wonderful_tu
172.17.0.2
```
[More info is here - Format command and log output](https://docs.docker.com/config/formatting/)

## Network management

Check network status:
* `docker network ls`
* `docker network inspect <network name>`

Get the list of networks.
* `--network bridge` is the default driver, it bridges ports from public to container.
* `--network host` gains performance by skipping virtual networks, but sacrifices security of container model.
* `--network none` - removes `eth0` and only leaves you with localhost interface in container

```bash
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
6b1dd7e8e55e        bridge              bridge              local
e5d21a0c2e64        docker_gwbridge     bridge              local
248812af76cf        host                host                local
rstocox5g68a        ingress             overlay             swarm
75kmerk6tqbs        mydrupal            overlay             swarm
4342d561491a        none                null                local
```

To inspect the driver and network. Check the containers on the network:
```bash
$ docker network inspect bridge
       ...
       "Containers": {
           "8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8": {
               "Name": "mysql",
               "EndpointID": "513f962ad4c2cf927fdcd6dbef8fbbb970a1ac08403c6046c30c746da3432ba9",
               "MacAddress": "02:42:ac:11:00:03",
               "IPv4Address": "172.17.0.3/16",
               "IPv6Address": ""
           },
           "a0e26dc95def1e77385df0c52714f519d5f4fdbb4c51e978c1a0eaa35efe1e48": {
               "Name": "wonderful_tu",
               "EndpointID": "f680ac408237c1887491cddc2859355d1f1f921e9110de23d70de9dd33b94f13",
               "MacAddress": "02:42:ac:11:00:02",
               "IPv4Address": "172.17.0.2/16",
               "IPv6Address": ""
           }
       }
       ...
```

## Creating Networks

Let's create a network (be default it will be created as `bridge`):
```bash
$ docker network create new_network
d6e24534dee90256120729bff3b20ec7181df966d5616566df3ec9ed1dbb0230

$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
6b1dd7e8e55e        bridge              bridge              local
e5d21a0c2e64        docker_gwbridge     bridge              local
248812af76cf        host                host                local
rstocox5g68a        ingress             overlay             swarm
75kmerk6tqbs        mydrupal            overlay             swarm
d6e24534dee9        new_network         bridge              local
4342d561491a        none                null                local
```

Run container inside the network:
```bash
$ docker container run -d --name new_nginx --network new_network nginx
b0d3565ec698174e3ee68eb854db011235cc2eb692143321116541aac48739e4

$ docker container ls
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                 NAMES
b0d3565ec698        nginx               "nginx -g 'daemon of…"   12 seconds ago      Up 9 seconds        80/tcp                new_nginx
8a06b5f5231b        mysql               "docker-entrypoint.s…"   3 hours ago         Up 3 hours          3306/tcp, 33060/tcp   mysql
a0e26dc95def        nginx               "nginx -g 'daemon of…"   3 hours ago         Up 3 hours          0.0.0.0:80->80/tcp    wonderful_tu

$ docker network inspect new_network
...
        "Containers": {
            "b0d3565ec698174e3ee68eb854db011235cc2eb692143321116541aac48739e4": {
                "Name": "new_nginx",
                "EndpointID": "2b00c85f206c88ff2c736f0f2e7ccb3c66f1079304033b86bcb5a56e6ab5876c",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        }
...
```

We can connect/disconnect container with network:
```bash
$ docker network connect --help

Usage:	docker network connect [OPTIONS] NETWORK CONTAINER

Connect a container to a network

Options:
      --alias strings           Add network-scoped alias for the container
      --driver-opt strings      driver options for the network
      --ip string               IPv4 address (e.g., 172.30.100.104)
      --ip6 string              IPv6 address (e.g., 2001:db8::33)
      --link list               Add link to another container
      --link-local-ip strings   Add a link-local address for the container
```
```bash
$ docker network disconnect --help

Usage:	docker network disconnect [OPTIONS] NETWORK CONTAINER

Disconnect a container from a network

Options:
        -f, --force   Force the container to disconnect from a network
```

Do do this, check network id:
```bash
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
6b1dd7e8e55e        bridge              bridge              local
e5d21a0c2e64        docker_gwbridge     bridge              local
248812af76cf        host                host                local
rstocox5g68a        ingress             overlay             swarm
75kmerk6tqbs        mydrupal            overlay             swarm
d6e24534dee9        new_network         bridge              local
4342d561491a        none                null                local
```

And connect the container. Check we got two containers now in the network:
```bash
$ docker network connect d6e24534dee9 wonderful_tu
$ docker network inspect new_network
...
        "Containers": {
            "a0e26dc95def1e77385df0c52714f519d5f4fdbb4c51e978c1a0eaa35efe1e48": {
                "Name": "wonderful_tu",
                "EndpointID": "fb5fe2c0ee24ff983bbf108eddd5578716f4c2a95a715a36ad255c446cb73bb5",
                "MacAddress": "02:42:ac:12:00:03",
                "IPv4Address": "172.18.0.3/16",
                "IPv6Address": ""
            },
            "b0d3565ec698174e3ee68eb854db011235cc2eb692143321116541aac48739e4": {
                "Name": "new_nginx",
                "EndpointID": "2b00c85f206c88ff2c736f0f2e7ccb3c66f1079304033b86bcb5a56e6ab5876c",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        }
...
```

Check the container is in two networks:
```bash
$ docker container inspect wonderful_tu
...
           "Networks": {
               "bridge": {
                   "IPAMConfig": null,
                   "Links": null,
                   "Aliases": null,
                   "NetworkID": "6b1dd7e8e55e06c13b04781eefb5754e90d63a0e1ff091d8e8ae4a432b5b5c37",
                   "EndpointID": "f680ac408237c1887491cddc2859355d1f1f921e9110de23d70de9dd33b94f13",
                   "Gateway": "172.17.0.1",
                   "IPAddress": "172.17.0.2",
                   "IPPrefixLen": 16,
                   "IPv6Gateway": "",
                   "GlobalIPv6Address": "",
                   "GlobalIPv6PrefixLen": 0,
                   "MacAddress": "02:42:ac:11:00:02",
                   "DriverOpts": null
               },
               "new_network": {
                   "IPAMConfig": {},
                   "Links": null,
                   "Aliases": [
                       "a0e26dc95def"
                   ],
                   "NetworkID": "d6e24534dee90256120729bff3b20ec7181df966d5616566df3ec9ed1dbb0230",
                   "EndpointID": "fb5fe2c0ee24ff983bbf108eddd5578716f4c2a95a715a36ad255c446cb73bb5",
                   "Gateway": "172.18.0.1",
                   "IPAddress": "172.18.0.3",
                   "IPPrefixLen": 16,
                   "IPv6Gateway": "",
                   "GlobalIPv6Address": "",
                   "GlobalIPv6PrefixLen": 0,
                   "MacAddress": "02:42:ac:12:00:03",
                   "DriverOpts": {}
               }
...
```

Now disconnect from old network:
```bash
$ docker network disconnect 6b1dd7e8e55e wonderful_tu

$ docker container inspect wonderful_tu
...
            "Networks": {
                "new_network": {
                    "IPAMConfig": {},
                    "Links": null,
                    "Aliases": [
                        "a0e26dc95def"
                    ],
                    "NetworkID": "d6e24534dee90256120729bff3b20ec7181df966d5616566df3ec9ed1dbb0230",
                    "EndpointID": "fb5fe2c0ee24ff983bbf108eddd5578716f4c2a95a715a36ad255c446cb73bb5",
                    "Gateway": "172.18.0.1",
                    "IPAddress": "172.18.0.3",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:12:00:03",
                    "DriverOpts": {}
                }
...
```
