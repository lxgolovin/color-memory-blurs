# What is swarm

## Problems to be solved

* How do we automate container lifecycle?
* How can we easily scale out/in/up/down?
* How can we ensure our containers are re-created if they fail?
* How can we replace containers with out downtime (blue/green deploy)?
* How can we control/track where containers get started?
* How can we create cross-node virtual networks?
* How can we ensure only trusted servers run out containers?
* How can we store secrets, keys, passwords and get them to the right container (and only that container)

## Swarm Mode: Build in Orchestration

* Swarm Mode is a clustering solution build inside Docker (could be different OS, solutions, any...)
* Added in 1.12 (Summer 2016) via SwarmKit toolkit
* Enchance in 1.12 (Jan 2017) via Stacks and Secrets
* Not enabled by default, new commands once enabled
    - `docker swarm`
    - `docker node`
    - `docker service`
    - `docker stack`
    - `docker secret`

## Swarm Basics

Lets do some basic stuff step by step.

### 1. Check Swarm

By default Swarm is disabled. Check this out with command:
```bash
$ docker info | grep -i swarm

 Swarm: inactive
```

### 2. Swarm command

Here is the scope of the commands:
```bash
$ docker swarm --help

Usage:	docker swarm COMMAND
Manage Swarm
Commands:
  ca          Display and rotate the root CA
  init        Initialize a swarm
  join        Join a swarm as a node and/or manager
  join-token  Manage join tokens
  leave       Leave the swarm
  unlock      Unlock swarm
  unlock-key  Manage the unlock key
  update      Update the swarm
```

To initialize Swarm:
```bash
$ docker swarm init

Swarm initialized: current node (ql9i9nguby2sckw1ln74vl7wy) is now a manager.
To add a worker to this swarm, run the following command:
    docker swarm join --token SWMTKN-1-08dfo5io73shublyke8tcdt67gh1eqk6uqpr0m1nra3k3sgdg7-b6s2206dk9uyoeeer9fkso0gq 10.0.2.15:2377
To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.
```
What happened when initialized:
* Lots of PKI and security automation
    - Root Signing Certificate created for our Swarm
    - Certificate is issued for first Manager node
    - Join tokens are created
* RAFT database created to store root CA, configs and secrets
    - Encrypted by default on disk (1.13+)
    - No need for another key/value system to hold orchestration/secrets
    - Replicates logs amongst Managers via mutual TLS in "control plane"

### 3. Check created nodes

Lets check what was created:
```bash
$ docker node ls

ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
ql9i9nguby2sckw1ln74vl7wy *   lxgolovin-VB        Ready               Active              Leader              19.03.8
```

More options
```bash
$ docker node --help

Usage:	docker node COMMAND
Manage Swarm nodes
Commands:
  demote      Demote one or more nodes from manager in the swarm
  inspect     Display detailed information on one or more nodes
  ls          List nodes in the swarm
  promote     Promote one or more nodes to manager in the swarm
  ps          List tasks running on one or more nodes, defaults to current node
  rm          Remove one or more nodes from the swarm
  update      Update a node
```

### 4. Create a service

Let's create a fake service:
```bash
$ docker service create alpine ping 8.8.8.8

zbu6kw7u9u0z7eyv2x7siobt7
overall progress: 0 out of 1 tasks
1/1: starting  [============================================>      ] overall progress: 1 out of 1 tasks
1/1: running   
verify: Service converged
```
Checkout the service.
```bash
$ docker service ls

ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
zbu6kw7u9u0z        vibrant_swartz      replicated          1/1                 alpine:latest       
```
The hash code is the same as in `create` command. Name `vibrant_swartz` was set randomly. Number of replicas `1/1` - means, the service already spun up one of one, the `1/` stands for how many are actually running, `/1` - one on the right - how many were specified to run. The goal of the orchestrator is to make these numbers match, whatever it takes.
Go on with checking:
```bash
$ docker service ps vibrant_swartz

ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
mmkkemwca9y5        vibrant_swartz.1    alpine:latest       lxgolovin-VB        Running             Running 14 minutes ago
```
Now we see the tasks or containers for the service. Actually it is almost the same as:
```bash
$ docker container ls

CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
1148887481ac        alpine:latest       "ping 8.8.8.8"      18 minutes ago      Up 18 minutes                           vibrant_swartz.1.mmkkemwca9y5x0oly1pstycmp
```

### 5. Update the service

If we want to update the service. Please, checkout `docker service update --help` - many-many options to try. One of them:
```bash
$ docker service update vibrant_swartz --replicas 3

vibrant_swartz
overall progress: 3 out of 3 tasks
1/3: running   [==================================================>]
2/3: running   [==================================================>]
3/3: running   [==================================================>]
verify: Service converged
```
And we get 3 of 3:
```bash
$ docker service ls

ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
zbu6kw7u9u0z        vibrant_swartz      replicated          3/3                 alpine:latest       
```
```bash
$ docker service ps vibrant_swartz

ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE            ERROR               PORTS
mmkkemwca9y5        vibrant_swartz.1    alpine:latest       lxgolovin-VB        Running             Running 24 minutes ago                       
3bigt3j8p6w2        vibrant_swartz.2    alpine:latest       lxgolovin-VB        Running             Running 2 minutes ago                        
mhumj5a9ydw0        vibrant_swartz.3    alpine:latest       lxgolovin-VB        Running             Running 2 minutes ago                        
```
```bash
$ docker container ls

CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
b4df310c1bdd        alpine:latest       "ping 8.8.8.8"      8 minutes ago       Up 8 minutes                            vibrant_swartz.2.3bigt3j8p6w243ov61qqoo3st
2fec9e9407cb        alpine:latest       "ping 8.8.8.8"      8 minutes ago       Up 8 minutes                            vibrant_swartz.3.mhumj5a9ydw0epst8wdqq5ms9
1148887481ac        alpine:latest       "ping 8.8.8.8"      31 minutes ago      Up 30 minutes                           vibrant_swartz.1.mmkkemwca9y5x0oly1pstycmp
```
Let us play a little with containers. Kill one of them with command `docker container rm -f vibrant_swartz.1.*` several times )))
```bash
$ docker service ls

ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
zbu6kw7u9u0z        vibrant_swartz      replicated          2/3                 alpine:latest
```
But in a few seconds:
```bash
$ docker service ls

ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
zbu6kw7u9u0z        vibrant_swartz      replicated          3/3                 alpine:latest
```
And check it with `service ps`
```bash
$ docker service ps vibrant_swartz

ID                  NAME                   IMAGE               NODE                DESIRED STATE       CURRENT STATE               ERROR                         PORTS
odcq5hhrev0d        vibrant_swartz.1       alpine:latest       lxgolovin-VB        Running             Running 35 seconds ago                                    
zd5c9frxbnvk         \_ vibrant_swartz.1   alpine:latest       lxgolovin-VB        Shutdown            Failed 42 seconds ago       "task: non-zero exit (137)"   
lh0683o5vuvv         \_ vibrant_swartz.1   alpine:latest       lxgolovin-VB        Shutdown            Failed about a minute ago   "task: non-zero exit (137)"   
mmkkemwca9y5         \_ vibrant_swartz.1   alpine:latest       lxgolovin-VB        Shutdown            Failed 2 minutes ago        "task: non-zero exit (137)"   
3bigt3j8p6w2        vibrant_swartz.2       alpine:latest       lxgolovin-VB        Running             Running 12 minutes ago                                    
mhumj5a9ydw0        vibrant_swartz.3       alpine:latest       lxgolovin-VB        Running             Running 12 minutes ago                                    
lxgolovin@lxgolovin-VB:~/qwerty$
```
They are back. And this is one of the responsibilities of the container orchestration system to make sure that the system is up with all the services. This differs from the docker run. Docker never recreates the container, but Swarm does. To remove the container, need to remove the service.

### 6. Remove the service

Do it:
```bash
$ docker service rm vibrant_swartz
vibrant_swartz
```
```bash
$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
```
Non left, good job!

## Some basic images to explain

![image][1] Concept of the swarm

![image][2] Concept example nginx

![image][3] What is in the backgroung of the service creation

## Usefull links

* [Docker 1.12 Swarm Mode Deep Dive Part 1: Topology][4]
* [Docker 1.12 Swarm Mode Deep Dive Part 2: Orchestration][5]
* [Heart of the SwarmKit: Topology Management][6]
* [Heart of the SwarmKit: Store, Topology & Object Model][7]
* [Raft: Understandable Distributed Consensus][8]
* [Deploy services to a swarm][9]
* [How-to Add SSH Keys to New or Existing Droplets ][10]
* [Docker Swarm Firewall Ports][11]
* [How To Configure Custom Connection Options for your SSH Client][12]
* [Microsoft Hyper-V][13]
* [Features, not supported in Stack Deploy][14]
* [Only one host for production environment. What to use: docker-compose or single node swarm?][15]
* [Manage sensitive data with Docker secrets][16]
* [Secrets in compose files][17]
* [Use swarm mode routing mesh][18]


[1]: ./img/swarm-behaviour.png
[2]: ./img/swarm-behaviour-usecase.png
[3]: ./img/swarm-inside.png
[4]: https://www.youtube.com/watch?v=dooPhkXT9yI
[5]: https://www.youtube.com/watch?v=_F6PSP-qhdA
[6]: https://speakerdeck.com/aluzzardi/heart-of-the-swarmkit-topology-management?slide=20
[7]: https://www.youtube.com/watch?v=EmePhjGnCXY
[8]: http://thesecretlivesofdata.com/raft/
[9]: https://docs.docker.com/engine/swarm/services/
[10]: https://www.digitalocean.com/docs/droplets/how-to/add-ssh-keys/
[11]: https://www.bretfisher.com/docker-swarm-firewall-ports/
[12]: https://www.digitalocean.com/community/tutorials/how-to-configure-custom-connection-options-for-your-ssh-client
[13]: https://docs.docker.com/machine/drivers/hyper-v/
[14]: https://docs.docker.com/compose/compose-file/#not-supported-for-docker-stack-deploy
[15]: https://github.com/BretFisher/ama/issues/8
[16]: https://docs.docker.com/engine/swarm/secrets/
[17]: https://docs.docker.com/compose/compose-file/#secrets-configuration-reference
[18]: https://docs.docker.com/engine/swarm/ingress
