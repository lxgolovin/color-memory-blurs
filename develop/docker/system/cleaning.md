# Using Prune to Keep Your Docker System Clean

## Image/Container prune

You can use `prune` commands to clean up images, volumes, build cache, and containers. Examples include:
```bash
# to clean up just "dangling" images
$ docker image prune
# to clean up just "dangling" containers
$ docker container prune
# to clean up just "dangling" volumes
$ docker volume prune
# will clean up everything
$ docker system prune
```
The big one is usually `docker image prune -a` which will remove all images you're not using. Use `docker system df` to see space usage.

> NOTE: Remember each one of those commands has options you can learn with --help

Interesting video from [BretFisher][1] is here at [YouTube][2]

Lastly, realize that if you're using Docker Toolbox, the Linux VM won't auto-shrink. You'll need to delete it and re-create (make sure anything in docker containers or volumes are backed up). You can recreate the toolbox default VM with `docker-machine rm default` and then `docker-machine create`

## System prune
To clean more use system prune
```bash
$ docker system prune
```
Output:
```
WARNING! This will remove:
  - all stopped containers
  - all networks not used by at least one container
  - all dangling images
  - all dangling build cache
```
This command does not clean all images. To do so, use `-a`:
```bash
$ docker system prune -a
```

[1]: https://github.com/BretFisher
[2]: https://youtu.be/_4QzP7uwtvI
