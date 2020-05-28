# Storing secrets in Swarm

## Secrets with Stacks

At the beginning we got 3 files:

```bash
$ echo "dbuser" > psql_user.txt
$ echo "password" > psql_password.txt
```

And docker compose file:
```yaml
version: "3.1" # version is at least

services:
  psql:
    image: postgres
    secrets:
      - psql_user
      - psql_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/psql_password
      POSTGRES_USER_FILE: /run/secrets/psql_user

secrets:
  psql_user:
    file: ./psql_user.txt
  psql_password:
    file: ./psql_password.tx
```

## Start the stack

```bash
$ docker stack deploy -c docker-compose.yml mydb
Creating secret mydb_psql_user
Creating secret mydb_psql_password
Creating service mydb_psql

$ docker secret ls
ID                          NAME                 DRIVER              CREATED             UPDATED
y9a8spfld2rtblehsu2t8lax2   mydb_psql_password                       45 seconds ago      45 seconds ago
m2n03b6uv25vwukjbieu60qqe   mydb_psql_user                           45 seconds ago      45 seconds ago
```

## Remove the Stack

```bash
$ docker stack rm mydb
Removing service mydb_psql
Removing secret mydb_psql_user
Removing secret mydb_psql_password
Removing network mydb_default

$ docker secret ls
ID                  NAME                DRIVER              CREATED             UPDATED
```

As one can see, secrets we also removed
