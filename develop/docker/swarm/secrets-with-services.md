# Storing secrets in Swarm

## Secrets with services

* As of Docker 1.13.0 Swarm Raft DB is encrypted on disk
* Only stored on disk on Manager nodes
* Secrets are first stored in Swarm, then assigned to a Service(s)
* Only containers in assigned Service(s) can see them
* They look like files in container but are actually in-memory fs
* `/run/secrets/<secret_name>` or `/run/secrets/<secret_alias>`
* Local docker-compose can use file-based secrets, but not secure
* this is Swarm only feature

## Starting with secrets

Here is the file with login
```bash
$ cat psql_user.txt
mypsqluser
```

Creating login/password
```bash
# saving in file
$ docker secret create psql_user psql_user.txt
bjgqjtsnkalka5fsxa65zlge7
# saving in memory
$ echo "Pas$w0rd" | docker secret create psql_pass -
3r094laohvp30ny4oxyd7l2ki
```

Lising of the secrets
```bash
$ docker secret ls
ID                          NAME                DRIVER              CREATED             UPDATED
3r094laohvp30ny4oxyd7l2ki   psql_pass                               4 seconds ago       4 seconds ago
bjgqjtsnkalka5fsxa65zlge7   psql_user                               15 seconds ago      15 seconds ago
```

We can also inspect them
```bash
$ docker secret inspect psql_pass
[
   {
       "ID": "3r094laohvp30ny4oxyd7l2ki",
       "Version": {
           "Index": 16
       },
       "CreatedAt": "2020-05-28T16:36:48.340842681Z",
       "UpdatedAt": "2020-05-28T16:36:48.340842681Z",
       "Spec": {
           "Name": "psql_pass",
           "Labels": {}
       }
   }
]

$ docker secret inspect psql_user
[
   {
       "ID": "bjgqjtsnkalka5fsxa65zlge7",
       "Version": {
           "Index": 15
       },
       "CreatedAt": "2020-05-28T16:36:37.884284626Z",
       "UpdatedAt": "2020-05-28T16:36:37.884284626Z",
       "Spec": {
           "Name": "psql_user",
           "Labels": {}
       }
   }
]
```

Let's create a service
```bash
$ docker service create --name psql --secret psql_user --secret psql_pass -e POSTGRES_PASSWORD_FILE=/run/secrets/psql_pass -e POSTGRES_USER_FILE=/run/secrets/psql_user postgres
is07xj8zuot7nu7pp90k64o9q
overall progress: 1 out of 1 tasks
1/1: running   [==================================================>]
verify: Service converged

$ docker service ps psql
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE           ERROR               PORTS
r9itmv6nkn72        psql.1              postgres:latest     master              Running             Running 2 minutes ago
```

Checkout the password and the login file
```bash
$ docker exec -it psql.1.r9itmv6nkn7241ol0wxe0ey0o bash
root@071d1b8476e9:/# ls -la /run/secrets/
total 20
drwxr-xr-x 2 root root 4096 May 28 16:40 .
drwxr-xr-x 1 root root 4096 May 28 16:40 ..
-r--r--r-- 1 root root    4 May 28 16:40 psql_pass
-r--r--r-- 1 root root   11 May 28 16:40 psql_user
root@071d1b8476e9:/# cat /run/secrets/*
Pas
mypsqluser
root@071d1b8476e9:/# exit
```

## Remove the secret

This is dangerous, as the container will restart and won't start back without the password:
```bash
$ docker service update --secret-rm psql_pass psql
psql
overall progress: 1 out of 1 tasks
1/1: running   [==================================================>]
verify: Waiting 5 seconds to verify that tasks are stable...
service update paused: update paused due to failure or early termination of task p091x22039hbk9r1qk4zucjax
```
