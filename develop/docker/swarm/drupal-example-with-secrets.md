# An example with drupal service with secrets

## Starting the service

```bash
$ docker stack deploy -c docker-compose.yml drupal
Creating network drupal_default
service postgres: secret not found: psql-pw

$ docker stack deploy -c docker-compose.yml drupal
service postgres: secret not found: psql-pw
```

To save the password:
```bash
$ echo "password" | docker secret create psql-pw -
64wf1c92w46ejumgerelwuwad
$ docker stack deploy -c docker-compose.yml drupal
Creating service drupal_postgres
Creating service drupal_drupal
$ docker stack ps drupal
ID                  NAME                IMAGE               NODE                DESIRED STATE       CURRENT STATE              ERROR               PORTS
gtuftswuolno        drupal_drupal.1     drupal:8.2          master              Running             Preparing 19 seconds ago
jqgoljo123a3        drupal_postgres.1   postgres:9.6        master              Running             Preparing 21 seconds ago
```

## Docker file

```yaml
version: '3.1' # minimum 3.1

services:

  drupal:
    image: drupal:8.2
    ports:
      - "8080:80"
    volumes:
      - drupal-modules:/var/www/html/modules
      - drupal-profiles:/var/www/html/profiles
      - drupal-sites:/var/www/html/sites
      - drupal-themes:/var/www/html/themes

  postgres:
    image: postgres:9.6
    environment:
      - POSTGRES_PASSWORD_FILE=/run/secrets/psql-pw
    secrets:
      - psql-pw
    volumes:
      - drupal-data:/var/lib/postgresql/data

volumes:
  drupal-data:
  drupal-modules:
  drupal-profiles:
  drupal-sites:
  drupal-themes:

secrets:
  psql-pw:
    external: true
```
