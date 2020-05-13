# Working with images

## Creating tags for the images

### 1. Listing images
```bash
$ docker image ls -a
```
Output:
```
REPOSITORY           TAG                 IMAGE ID            CREATED             SIZE
mysql                latest              8e8c6f8dc9df        22 hours ago        546MB
nginx                latest              ed21b7a8aee9        2 weeks ago         127MB
alpine               latest              a187dde48cd2        3 weeks ago         5.6MB
nginx                <none>              a1523e859360        7 weeks ago         127MB
centos               latest              470671670cac        3 months ago        237MB
mysql/mysql-server   latest              a7a39f15d42d        3 months ago        381MB
elasticsearch        2                   5e9d896dc62c        19 months ago       479MB
```
### 2. Creating tag for the image
```bash
$ docker image tag nginx:latest lxgolovin/nginx
```

### 3. Check the listing again
```bash
$ docker image ls
```
Output:
```
REPOSITORY           TAG                 IMAGE ID            CREATED             SIZE
mysql                latest              8e8c6f8dc9df        22 hours ago        546MB
nginx                latest              ed21b7a8aee9        2 weeks ago         127MB
lxgolovin/nginx      latest              ed21b7a8aee9        2 weeks ago         127MB
alpine               latest              a187dde48cd2        3 weeks ago         5.6MB
nginx                <none>              a1523e859360        7 weeks ago         127MB
centos               latest              470671670cac        3 months ago        237MB
mysql/mysql-server   latest              a7a39f15d42d        3 months ago        381MB
elasticsearch        2                   5e9d896dc62c        19 months ago       479MB
```

### 4. Try to push the image to repo
```bash
$ docker image push lxgolovin/nginx
```
If not configured (not logined):
```
The push refers to repository [docker.io/lxgolovin/nginx]
d37eecb5b769: Preparing
99134ec7f247: Preparing
c3a984abe8a8: Preparing
denied: requested access to the resource is denied
```

### 5. Login to docker repo if not done before
```bash
$ docker login
```
Enter login and password. A new file will be created:
```
WARNING! Your password will be stored unencrypted in ~/.docker/config.json.
Login Succeeded
```
Checkout the file `~/.docker/config.json`:
```
{
	"auths": {
		"https://index.docker.io/v1/": {
			"auth": "bHhvdmluOkEkNA=="
		}
	},
	"HttpHeaders": {
		"User-Agent": "Docker-Client/19.03.8 (linux)"
	}
}
```
>Warning:
>Should logout when working on shared machine
