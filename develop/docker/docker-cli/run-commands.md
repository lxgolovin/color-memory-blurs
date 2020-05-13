# Basic Docker CLI run commands

## Docker run

Run a container:
```bash
$ docker container run --publish 80:80 nginx
Unable to find image 'nginx:latest' locally
latest: Pulling from library/nginx
...
```
> Note: Old style is `docker run ...`

What that does:
* Downloaded image 'nginx' from Docker Hub
* Started a new container from that image
* Opened port 80 on the host IP
* Routes that traffic to the container IP, port 80

Same as previous, but in background (returns hash of the container):
`docker container run --publish 80:80 --detach nginx`

If need to set a name (ususally it is generated randomly):
`docker container run --publish 80:80 --detach --name <name of the container> nginx`

To check the one is running:
```bash
$ docker container ls
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                NAMES
a0e26dc95def        nginx               "nginx -g 'daemon of…"   10 seconds ago      Up 6 seconds        0.0.0.0:80->80/tcp   wonderful_tu
```

To start interactive, use `-it` option:
```bash
$ docker container run -it --name proxy nginx bash
root@ae604847f762:/# pwd
/
root@ae604847f762:/# exit
exit
# container stops!!!

$ docker container ls -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                         PORTS                 NAMES
ae604847f762        nginx               "bash"                   25 seconds ago      Exited (0) 12 seconds ago                            proxy
8a06b5f5231b        mysql               "docker-entrypoint.s…"   About an hour ago   Up About an hour               3306/tcp, 33060/tcp   mysql
9cb8f0e5d92a        nginx               "nginx -g 'daemon of…"   About an hour ago   Exited (0) About an hour ago                         nginx
a774bf1f8e79        nginx               "--name wonderful_tu"    2 hours ago         Created                        0.0.0.0:80->80/tcp    condescending_rosalind
a0e26dc95def        nginx               "nginx -g 'daemon of…"   2 hours ago         Up About an hour               0.0.0.0:80->80/tcp    wonderful_tu
84e7d4f605db        nginx               "nginx -g 'daemon of…"   2 hours ago         Exited (0) 2 hours ago                               dreamy_cray
```
[More info with ubuntu example](./interactive-ubuntu-example.md)

## Docker start existing

Start the container:
```bash
$ docker container start wonderful_tu
wonderful_tu
```
> Note: old style `docker start`

## Docker stop

Stop the container:
```bash
$ docker container stop wonderful_tu
wonderful_tu
```
> Note: old style `docker stop`

## Docker container information

Listing containers
```bash
# no container in usual ls command if stopped
$ docker container ls
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES

# to see stopped containers ls -a
$ docker container ls -a
CONTAINER ID        IMAGE               COMMAND                  CREATED              STATUS                          PORTS               NAMES
a0e26dc95def        nginx               "nginx -g 'daemon of…"   About a minute ago   Exited (0) 8 seconds ago                            wonderful_tu
84e7d4f605db        nginx               "nginx -g 'daemon of…"   11 minutes ago       Exited (0) About a minute ago                       dreamy_cray
```
> Note: old version for listing is `docker ps`

To check the logs:
```bash
$ docker container logs nginx
172.17.0.1 - - [05/May/2020:14:42:08 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.58.0" "-"
```

To check them online:
```bash
$ docker container logs -f nginx
172.17.0.1 - - [05/May/2020:14:42:08 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.58.0" "-"
```

> Note: old style `docker logs`

To check processes running:
```bash
$ docker container top nginx
UID                 PID                 PPID                C                   STIME               TTY                 TIME                CMD
root                5677                5651                0                   17:41               ?                   00:00:00            nginx: master process nginx -g daemon off;
systemd+            5724                5677                0                   17:41               ?                   00:00:00            nginx: worker process
```

> Note: old style `docker top`

To check processes:
```bash
$ docker container stats
CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT    MEM %               NET I/O             BLOCK I/O           PIDS
8a06b5f5231b        mysql               0.60%               319.8MiB / 3.85GiB   8.11%               5.64kB / 0B         8.94MB / 248MB      38
a0e26dc95def        wonderful_tu        0.00%               1.957MiB / 3.85GiB   0.05%               6.07kB / 0B         0B / 0B             2
```
> Note: old style `docker stats`

Docker inspect example:
```bash
$ docker container inspect mysql
[
    {
        "Id": "8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8",
        "Created": "2020-05-05T14:58:13.214756225Z",
        "Path": "docker-entrypoint.sh",
        "Args": [
            "mysqld"
        ],
        "State": {
            "Status": "running",
            "Running": true,
            "Paused": false,
            "Restarting": false,
            "OOMKilled": false,
            "Dead": false,
            "Pid": 7088,
            "ExitCode": 0,
            "Error": "",
            "StartedAt": "2020-05-05T14:58:16.750468025Z",
            "FinishedAt": "0001-01-01T00:00:00Z"
        },
        "Image": "sha256:a7a67c95e83189d60dd24cfeb13d9f235a95a7afd7749a7d09845f303fab239c",
        "ResolvConfPath": "/var/lib/docker/containers/8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8/resolv.conf",
        "HostnamePath": "/var/lib/docker/containers/8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8/hostname",
        "HostsPath": "/var/lib/docker/containers/8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8/hosts",
        "LogPath": "/var/lib/docker/containers/8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8/8a06b5f5231b4642bcba993b841c59a01ac2d3aeca688845a0697b97348c64a8-json.log",
        "Name": "/mysql",
        "RestartCount": 0,
        "Driver": "overlay2",
        "Platform": "linux",
        "MountLabel": "",
        "ProcessLabel": "",
        "AppArmorProfile": "docker-default",
        "ExecIDs": null,
        "HostConfig": {
            "Binds": null,
            "ContainerIDFile": "",
            "LogConfig": {
                "Type": "json-file",
                "Config": {}
            },
            "NetworkMode": "default",
            "PortBindings": {},
            "RestartPolicy": {
                "Name": "no",
                "MaximumRetryCount": 0
            },
            "AutoRemove": false,
            "VolumeDriver": "",
            "VolumesFrom": null,
            "CapAdd": null,
            "CapDrop": null,
            "Capabilities": null,
            "Dns": [],
            "DnsOptions": [],
            "DnsSearch": [],
            "ExtraHosts": null,
            "GroupAdd": null,
            "IpcMode": "private",
            "Cgroup": "",
            "Links": null,
            "OomScoreAdj": 0,
            "PidMode": "",
            "Privileged": false,
            "PublishAllPorts": false,
            "ReadonlyRootfs": false,
            "SecurityOpt": null,
            "UTSMode": "",
            "UsernsMode": "",
            "ShmSize": 67108864,
            "Runtime": "runc",
            "ConsoleSize": [
                0,
                0
            ],
            "Isolation": "",
            "CpuShares": 0,
            "Memory": 0,
            "NanoCpus": 0,
            "CgroupParent": "",
            "BlkioWeight": 0,
            "BlkioWeightDevice": [],
            "BlkioDeviceReadBps": null,
            "BlkioDeviceWriteBps": null,
            "BlkioDeviceReadIOps": null,
            "BlkioDeviceWriteIOps": null,
            "CpuPeriod": 0,
            "CpuQuota": 0,
            "CpuRealtimePeriod": 0,
            "CpuRealtimeRuntime": 0,
            "CpusetCpus": "",
            "CpusetMems": "",
            "Devices": [],
            "DeviceCgroupRules": null,
            "DeviceRequests": null,
            "KernelMemory": 0,
            "KernelMemoryTCP": 0,
            "MemoryReservation": 0,
            "MemorySwap": 0,
            "MemorySwappiness": null,
            "OomKillDisable": false,
            "PidsLimit": null,
            "Ulimits": null,
            "CpuCount": 0,
            "CpuPercent": 0,
            "IOMaximumIOps": 0,
            "IOMaximumBandwidth": 0,
            "MaskedPaths": [
                "/proc/asound",
                "/proc/acpi",
                "/proc/kcore",
                "/proc/keys",
                "/proc/latency_stats",
                "/proc/timer_list",
                "/proc/timer_stats",
                "/proc/sched_debug",
                "/proc/scsi",
                "/sys/firmware"
            ],
            "ReadonlyPaths": [
                "/proc/bus",
                "/proc/fs",
                "/proc/irq",
                "/proc/sys",
                "/proc/sysrq-trigger"
            ]
        },
        "GraphDriver": {
            "Data": {
                "LowerDir": "/var/lib/docker/overlay2/9d9a89ae8cd76415f2a9b72d56e51e464d5588d17e5b0193747d6e4ccaa72a72-init/diff:/var/lib/docker/overlay2/709b4e6ddf8e189e9dca0b7eaf71a1a2abf21ce7a504be59feaeb569bdf9ba72/diff:/var/lib/docker/overlay2/4a4d18894a7f51d5783e1c17908288227abf9d03e4b61120657e1cfeeb9bc8d6/diff:/var/lib/docker/overlay2/7eff294e9bdfcc12fb7dc7a0319581aaf7b8ff325bc2602f814e053fcb779f46/diff:/var/lib/docker/overlay2/37997077aa64600b3723c44ac17ffc07c6c1ea79059a33a329fefe8f2668c42c/diff:/var/lib/docker/overlay2/9316045a3bf340fddb47c1e0e212ca4f54fec9dba5fbe7daf2dac78c814d61da/diff:/var/lib/docker/overlay2/ad94aad191bd6366b0f00f08acb6abd573e15134b3983c790002db0c3b0286ac/diff:/var/lib/docker/overlay2/02d49cc5b5e30daa0eda638393a022d30fc7cbb3697e23ab7f49317817fccc8a/diff:/var/lib/docker/overlay2/57600c74867618496bad04703f2eb374460100048ab1ae83ec6e893707fe4c27/diff:/var/lib/docker/overlay2/256d2e462c143710fdbc8c0470940566291a7a71751f0e8fa84876e0ed40a38b/diff:/var/lib/docker/overlay2/2f185e302bf0751b9349f5afc234faf3e0383bae637f8c5943842f334c9df109/diff:/var/lib/docker/overlay2/e3d77fa293a18a3668f19fe30a212084bdb7e403a25e5be54ce1eb6c1c59fed0/diff:/var/lib/docker/overlay2/86a7c7c6cb44a938fa36d561207911d95e3623989cb2974774040dccbc487cfa/diff",
                "MergedDir": "/var/lib/docker/overlay2/9d9a89ae8cd76415f2a9b72d56e51e464d5588d17e5b0193747d6e4ccaa72a72/merged",
                "UpperDir": "/var/lib/docker/overlay2/9d9a89ae8cd76415f2a9b72d56e51e464d5588d17e5b0193747d6e4ccaa72a72/diff",
                "WorkDir": "/var/lib/docker/overlay2/9d9a89ae8cd76415f2a9b72d56e51e464d5588d17e5b0193747d6e4ccaa72a72/work"
            },
            "Name": "overlay2"
        },
        "Mounts": [
            {
                "Type": "volume",
                "Name": "c9ce2934c8bdbd38c6927e0cac9a7a4a43f4b0189a4c6444b322ab3723d0740a",
                "Source": "/var/lib/docker/volumes/c9ce2934c8bdbd38c6927e0cac9a7a4a43f4b0189a4c6444b322ab3723d0740a/_data",
                "Destination": "/var/lib/mysql",
                "Driver": "local",
                "Mode": "",
                "RW": true,
                "Propagation": ""
            }
        ],
        "Config": {
            "Hostname": "8a06b5f5231b",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "ExposedPorts": {
                "3306/tcp": {},
                "33060/tcp": {}
            },
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": [
                "MYSQL_RANDOM_ROOT_PASSWORD=true",
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "GOSU_VERSION=1.12",
                "MYSQL_MAJOR=8.0",
                "MYSQL_VERSION=8.0.20-1debian10"
            ],
            "Cmd": [
                "mysqld"
            ],
            "Image": "mysql",
            "Volumes": {
                "/var/lib/mysql": {}
            },
            "WorkingDir": "",
            "Entrypoint": [
                "docker-entrypoint.sh"
            ],
            "OnBuild": null,
            "Labels": {}
        },
        "NetworkSettings": {
            "Bridge": "",
            "SandboxID": "f11d57a75bd67e8779ffcd626edbc47b25bab765f6f050196ccc2d97315db7bb",
            "HairpinMode": false,
            "LinkLocalIPv6Address": "",
            "LinkLocalIPv6PrefixLen": 0,
            "Ports": {
                "3306/tcp": null,
                "33060/tcp": null
            },
            "SandboxKey": "/var/run/docker/netns/f11d57a75bd6",
            "SecondaryIPAddresses": null,
            "SecondaryIPv6Addresses": null,
            "EndpointID": "513f962ad4c2cf927fdcd6dbef8fbbb970a1ac08403c6046c30c746da3432ba9",
            "Gateway": "172.17.0.1",
            "GlobalIPv6Address": "",
            "GlobalIPv6PrefixLen": 0,
            "IPAddress": "172.17.0.3",
            "IPPrefixLen": 16,
            "IPv6Gateway": "",
            "MacAddress": "02:42:ac:11:00:03",
            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "6b1dd7e8e55e06c13b04781eefb5754e90d63a0e1ff091d8e8ae4a432b5b5c37",
                    "EndpointID": "513f962ad4c2cf927fdcd6dbef8fbbb970a1ac08403c6046c30c746da3432ba9",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.3",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:11:00:03",
                    "DriverOpts": null
                }
            }
        }
    }
]
```
> Note: View config for the container
>
> Old style: `docker inspect <name of the container> or <hash code>`
>
> New style: `docker container inspect <name of the container> or <hash code>`

## Docker removing

To remove stopped container:
`docker container rm <name of the container> or <hash code>`
> Note: old style: `docker rm <name of the container> or <hash code>`

To remove running container:
`docker container rm -f <name of the container> or <hash code>`

Remove the container:
```bash
$ docker container stop wonderful_tu
wonderful_tu
```
