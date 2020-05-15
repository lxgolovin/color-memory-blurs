# Manage data in Docker

## Volumes
Volumes are stored in a part of the host filesystem which is managed by Docker (`/var/lib/docker/volumes/` on Linux). Non-Docker processes should not modify this part of the filesystem. Volumes are the best way to persist data in Docker.
Created and managed by Docker. You can create a volume explicitly using the docker volume create command, or Docker can create a volume during container or service creation.

When you create a volume, it is stored within a directory on the Docker host. When you mount the volume into a container, this directory is what is mounted into the container. This is similar to the way that bind mounts work, except that volumes are managed by Docker and are isolated from the core functionality of the host machine.

A given volume can be mounted into multiple containers simultaneously. When no running container is using a volume, the volume is still available to Docker and is not removed automatically. You can remove unused volumes using `docker volume prune`.

When you mount a volume, it may be named or anonymous. Anonymous volumes are not given an explicit name when they are first mounted into a container, so Docker gives them a random name that is guaranteed to be unique within a given Docker host. Besides the name, named and anonymous volumes behave in the same ways.

Volumes also support the use of volume drivers, which allow you to store your data on remote hosts or cloud providers, among other possibilities.

Example:
```bash
$ docker image inspect mysql
```
Checkout for:
```
...
"Volumes": {
    "/var/lib/mysql": {}
}
...
```
Running container:
```bash
$ docker container run -d --name mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=True mysql
$ docker container inspect mysql
$ docker volume ls
```
Checkout:
```
...
"Mounts": [
    {
        "Type": "volume",
        "Name": "77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b",
        "Source": "/var/lib/docker/volumes/77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b/_data",
        "Destination": "/var/lib/mysql",
        "Driver": "local",
        "Mode": "",
        "RW": true,
        "Propagation": ""
    }
]

...

DRIVER              VOLUME NAME
local               7a73bd1d81e94ee096f5ed4f9ed379bde8387cf138abc916f0383b1d89693e19
local               77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b
local               373db47421834b47eccbca2f53f83d72a7c5a0a55b63724d6bb3164275a5ca4a
```
Get information of the volume
```bash
$ docker volume  inspect 77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b
```
The info looks like this:
```
[
    {
        "CreatedAt": "2020-04-21T06:57:02+03:00",
        "Driver": "local",
        "Labels": null,
        "Mountpoint": "/var/lib/docker/volumes/77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b/_data",
        "Name": "77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b",
        "Options": null,
        "Scope": "local"
    }
]
```
Here one can find files:
```bash
$ sudo ls -la /var/lib/docker/volumes/77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b/_data
```
To set the name of the volume, one can use `-v` option
```bash
$ docker container run -d --name mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=True -v mysql-db:/var/lib/mysql mysql
$ docker volume ls
```
Find it:
```
DRIVER              VOLUME NAME
local               7a73bd1d81e94ee096f5ed4f9ed379bde8387cf138abc916f0383b1d89693e19
local               77b8aeb135b75d11656abfd87207e842f015e4fc0da8b97c78f6d74de026708b
local               373db47421834b47eccbca2f53f83d72a7c5a0a55b63724d6bb3164275a5ca4a
local               mysql-db
```
To mount other container with that volume:
```bash
$ docker volume inspect mysql-db
$ docker container rm -f mysql
$ docker container run -d --name mysql2 -e MYSQL_ALLOW_EMPTY_PASSWORD=True -v mysql-db:/var/lib/mysql mysql
$ docker volume ls
$ docker container inspect mysql3
```
Mounts for both containers same
```
...
"Mounts": [
    {
        "Type": "volume",
        "Name": "mysql-db",
        "Source": "/var/lib/docker/volumes/mysql-db/_data",
        "Destination": "/var/lib/mysql",
        "Driver": "local",
        "Mode": "z",
        "RW": true,
        "Propagation": ""
    }
]
...
```
As an example, level up the postgres:
```bash
$ docker container run -d --name psql -v psql:/var/lib/postgresql/data postgres:9.6.1
# tail -f for logs
$ docker container logs -f psql
# mounts to same directory
$ docker container run -d --name psql2 -v psql:/var/lib/postgresql/data postgres:9.6.2
```
When running postgres now, you'll possibly need to either set a password, or tell it to allow any connection (which was the default before).

For docker run, you need to either set a password with the environment variable: `POSTGRES_PASSWORD=mypasswd`

Or tell it to ignore passwords with the environment variable: `POSTGRES_HOST_AUTH_METHOD=trust`. Here is an example:
```bash
$ docker container run -d --name psql -e POSTGRES_HOST_AUTH_METHOD=trust -v psql:/var/lib/postgresql/data postgres:9.6.1
```

Also `docker volume create` could be used. But as for me, not so powerful option

## Bind mounts
Available since the early days of Docker. Bind mounts have limited functionality compared to volumes. When you use a bind mount, a file or directory on the host machine is mounted into a container. The file or directory is referenced by its full path on the host machine. The file or directory does not need to exist on the Docker host already. It is created on demand if it does not yet exist. Bind mounts are very performant, but they rely on the host machine’s filesystem having a specific directory structure available. If you are developing new Docker applications, consider using named volumes instead. You can’t use Docker CLI commands to directly manage bind mounts.

Small example with nginx:
```bash
# bound with local fs
$ docker container run -d --name nginx -p 80:80 -v $(pwd):/usr/share/nginx/html nginx
# not bound with local fs
$ docker container run -d --name nginx2 -p 8080:80 nginx

$ docker container exec -it nginx bash
```
There are some shell differences for path expansion. With Docker CLI, you can always use a full file path on any OS, but better use a "parameter expansion" like `$(pwd)` for "working directory".

Here's the important part. Each shell may do this differently, so here's a cheat sheet for which OS and Shell your using. For Linux and Mac using `$(pwd)` is ok, but...

For PowerShell use: `${pwd}`

For cmd.exe command prompt use: `%cd%`

Linux/macOS bash, sh, zsh, and Windows Docker Toolbox Quickstart Terminal use: $(pwd)

>Note, if you have spaces in your path, you'll usually need to quote the whole path in the docker command.

------------------------------------------------------------------

>Bind mounts allow access to sensitive files
>One side effect of using bind mounts, for better or for worse, is that you can change the host filesystem via processes running in a container, including creating, modifying, or deleting important system files or directories. This is a powerful ability which can have security implications, including impacting non-Docker processes on the host system.

## Useful links

* [Docker docs][1] - useful docker docs
* [12 Fractured Apps][2] - nice article
* [An introduction to immutable infrastructure][3] - Why you should stop managing infrastructure and start really programming it

[1]: https://docs.docker.com/storage/
[2]: https://medium.com/@kelseyhightower/12-fractured-apps-1080c73d481c#.cjvkgw4b3
[3]: https://www.oreilly.com/radar/an-introduction-to-immutable-infrastructure/
