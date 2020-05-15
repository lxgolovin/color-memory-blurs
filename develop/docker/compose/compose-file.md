# Docker compose

## Small introduction

Compose is a tool for defining and running multi-container Docker applications. With Compose, you use a YAML file to configure your application’s services. Then, with a single command, you create and start all the services from your configuration.

Using Compose is basically a three-step process:
* Define your app’s environment with a `Dockerfile` so it can be reproduced anywhere.
* Define the services that make up your app in `docker-compose.yml` so they can be run together in an isolated environment.
* Run `docker-compose up` and Compose starts and runs your entire app.

`docker-compose.yml` is the default name of the yml file. But could be changed with `docker-compose -f new-file-name`

CLI tool comes wit docker for Windows/Mac, but separate download for Linux.

## Most common commands are

* `docker-compose up` - to setup volumes/networks and start all containers
* `docker-compose down` - to stop all containers and remove containers/volumes/network. More options for the command:
```
    --rmi type              Remove images. Type must be one of:
                            'all': Remove all images used by any service.
                            'local': Remove only images that don't have a
                            custom tag set by the `image` field.
    -v, --volumes           Remove named volumes declared in the `volumes`
                            section of the Compose file and anonymous volumes
                            attached to containers.
    --remove-orphans        Remove containers for services not defined in the
                            Compose file
    -t, --timeout TIMEOUT   Specify a shutdown timeout in seconds.
```
* `docker-compose logs` - to check logs is they are not on the screen (when compose started with `-d` option)
* `docker-compose ps` - to see the list of the containers
```
Name              Command          State         Ports       
qwerty_proxy_1   nginx -g daemon off;   Up      0.0.0.0:80->80/tcp
qwerty_web_1     httpd-foreground       Up      80/tcp   
```
* `docker-compose top` - to list processes
```
qwerty_proxy_1
  UID      PID    PPID   C   STIME   TTY     TIME                        CMD                    
root       3810   3786   1   08:09   ?     00:00:02   nginx: master process nginx -g daemon off;
systemd+   4021   3810   0   08:09   ?     00:00:00   nginx: worker process                     
qwerty_web_1
 UID     PID    PPID   C   STIME   TTY     TIME            CMD        
root     3756   3718   1   08:09   ?     00:00:02   httpd -DFOREGROUND
daemon   3925   3756   0   08:09   ?     00:00:00   httpd -DFOREGROUND
daemon   3926   3756   0   08:09   ?     00:00:00   httpd -DFOREGROUND
daemon   3927   3756   0   08:09   ?     00:00:00   httpd -DFOREGROUND
```

>Docker compose is not a production-grade tool, but ideal for local development and testing

More commands for docker-compose cli in help:
```
Commands:
  build              Build or rebuild services
  config             Validate and view the Compose file
  create             Create services
  down               Stop and remove containers, networks, images, and volumes
  events             Receive real time events from containers
  exec               Execute a command in a running container
  help               Get help on a command
  images             List images
  kill               Kill containers
  logs               View output from containers
  pause              Pause services
  port               Print the public port for a port binding
  ps                 List containers
  pull               Pull service images
  push               Push service images
  restart            Restart services
  rm                 Remove stopped containers
  run                Run a one-off command
  scale              Set number of containers for a service
  start              Start services
  stop               Stop services
  top                Display the running processes
  unpause            Unpause services
  up                 Create and start containers
  version            Show the Docker-Compose version information
```
## Docker compose files

Small docker compose file template:
```yml
version: '3.1'  # if no version is specified then v1 is assumed. Recommend v2 minimum

services:  # containers. same as docker run
  servicename: # a friendly name. this is also DNS name inside network
    image: # Optional if you use build:
    command: # Optional, replace the default CMD specified by the image
    environment: # Optional, same as -e in docker run
    volumes: # Optional, same as -v in docker run
  servicename2:
volumes: # Optional, same as docker volume create
networks: # Optional, same as docker network create
```

Docker compose example with a cluster:
```yml
version: '3'

services:
  ghost:
    image: ghost
    ports:
      - "80:2368"
    environment:
      - URL=http://localhost
      - NODE_ENV=production
      - MYSQL_HOST=mysql-primary
      - MYSQL_PASSWORD=mypass
      - MYSQL_DATABASE=ghost
    volumes:
      - ./config.js:/var/lib/ghost/config.js
    depends_on:
      - mysql-primary
      - mysql-secondary
  proxysql:
    image: percona/proxysql
    environment:
      - CLUSTER_NAME=mycluster
      - CLUSTER_JOIN=mysql-primary,mysql-secondary
      - MYSQL_ROOT_PASSWORD=mypass

      - MYSQL_PROXY_USER=proxyuser
      - MYSQL_PROXY_PASSWORD=s3cret
  mysql-primary:
    image: percona/percona-xtradb-cluster:5.7
    environment:
      - CLUSTER_NAME=mycluster
      - MYSQL_ROOT_PASSWORD=mypass
      - MYSQL_DATABASE=ghost
      - MYSQL_PROXY_USER=proxyuser
      - MYSQL_PROXY_PASSWORD=s3cret
  mysql-secondary:
    image: percona/percona-xtradb-cluster:5.7
    environment:
      - CLUSTER_NAME=mycluster
      - MYSQL_ROOT_PASSWORD=mypass

      - CLUSTER_JOIN=mysql-primary
      - MYSQL_PROXY_USER=proxyuser
      - MYSQL_PROXY_PASSWORD=s3cret
    depends_on:
      - mysql-primary
```

## Useful links

* [yaml introduction][1]
* [yaml features and syntax][2]
* [github link to docker compose][3]
* [docker compose file versioning][4]
* [docker compose file syntax][5]
* [build with docker][6]
* [compose s swarm][7]

[1]: https://yaml.org/start.html
[2]: https://yaml.org/refcard.html
[3]: https://github.com/docker/compose/releases
[4]: https://docs.docker.com/compose/compose-file/compose-versioning/
[5]: https://docs.docker.com/compose/compose-file
[6]: https://docs.docker.com/compose/compose-file/#build
[7]: https://github.com/BretFisher/ama/issues/8
