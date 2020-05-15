# Interactive ubuntu example

## Get the image

```bash
$ docker pull ubuntu
Using default tag: latest
latest: Pulling from library/ubuntu
```

## Run new container interactive

```bash
$ docker container run -it --name ubuntu ubuntu
root@bcf16232b005:/# which curl
root@bcf16232b005:/# apt update
root@bcf16232b005:/# apt-get install curl -y
root@bcf16232b005:/# which curl
/usr/bin/curl
root@bcf16232b005:/# exit
exit
```
Currently we stopped the container

## Start the container again

Now we can see that `curl` already installed.
```bash
$ docker start -ai ubuntu
root@bcf16232b005:/# which curl
/usr/bin/curl
root@bcf16232b005:/# exit
exit

$ docker container ls -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                          PORTS                 NAMES
bcf16232b005        ubuntu              "/bin/bash"              8 minutes ago       Exited (0) About a minute ago                         ubuntu
ae604847f762        nginx               "bash"                   15 minutes ago      Exited (0) 14 minutes ago                             proxy
8a06b5f5231b        mysql               "docker-entrypoint.s…"   About an hour ago   Up About an hour                3306/tcp, 33060/tcp   mysql
9cb8f0e5d92a        nginx               "nginx -g 'daemon of…"   2 hours ago         Exited (0) 2 hours ago                                nginx
a774bf1f8e79        nginx               "--name wonderful_tu"    2 hours ago         Created                         0.0.0.0:80->80/tcp    condescending_rosalind
a0e26dc95def        nginx               "nginx -g 'daemon of…"   2 hours ago         Up 2 hours                      0.0.0.0:80->80/tcp    wonderful_tu
84e7d4f605db        nginx               "nginx -g 'daemon of…"   2 hours ago         Exited (0) 2 hours ago                                dreamy_cray

```

## Connect to running container

To do this, use `exec` command
```bash
$ docker container exec --help

Usage:	docker container exec [OPTIONS] CONTAINER COMMAND [ARG...]

Run a command in a running container

Options:
  -d, --detach               Detached mode: run command in the background
      --detach-keys string   Override the key sequence for detaching a container
  -e, --env list             Set environment variables
  -i, --interactive          Keep STDIN open even if not attached
      --privileged           Give extended privileges to the command
  -t, --tty                  Allocate a pseudo-TTY
  -u, --user string          Username or UID (format: <name|uid>[:<group|gid>])
  -w, --workdir string       Working directory inside the container
```

Let's check it with running one:
```bash
$ docker container exec -it mysql bash
root@8a06b5f5231b:/# exit
exit

$ docker container ls
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                 NAMES
8a06b5f5231b        mysql               "docker-entrypoint.s…"   2 hours ago         Up 2 hours          3306/tcp, 33060/tcp   mysql
a0e26dc95def        nginx               "nginx -g 'daemon of…"   2 hours ago         Up 2 hours          0.0.0.0:80->80/tcp    wonderful_tu

```
The container is still running, as we used `exec`
