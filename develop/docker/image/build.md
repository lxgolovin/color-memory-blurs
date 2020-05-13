# Building using Dockerfile

## Usual build
1. Create a Dockerfile inside the directory
2. Launch build image command
```bash
$ docker image build -t customname .
```
Output after second launch if nothing changed
```
Sending build context to Docker daemon  4.096kB
Step 1/7 : FROM debian:stretch-slim
 ---> 054366b4bf12
Step 2/7 : ENV NGINX_VERSION 1.13.6-1~stretch
 ---> Using cache
 ---> 4e9431a424a0
Step 3/7 : ENV NJS_VERSION   1.13.6.0.1.14-1~stretch
 ---> Using cache
 ---> 3a786aa7fb59
Step 4/7 : RUN apt-get update 	&& apt-get install --no-install-recommends --no-install-suggests -y gnupg1 	&& 	apt-get remove --purge -y gnupg1 && apt-get -y --purge autoremove && rm -rf /var/lib/apt/lists/* 	&& apt-get update 	&& apt-get install nginx -y
 ---> Using cache
 ---> f2b0cd8641fd
Step 5/7 : RUN ln -sf /dev/stdout /var/log/nginx/access.log 	&& ln -sf /dev/stderr /var/log/nginx/error.log
 ---> Using cache
 ---> 8d433ab6eefd
Step 6/7 : EXPOSE 80 443
 ---> Using cache
 ---> 64b9e3cf6ed2
Step 7/7 : CMD ["nginx", "-g", "daemon off;"]
 ---> Using cache
 ---> 2f77eb68cf94
Successfully built 2f77eb68cf94
Successfully tagged customname:latest
```
If some step corrected, it will rebuild that step only
```
Sending build context to Docker daemon  4.096kB
Step 1/7 : FROM debian:stretch-slim
 ---> 054366b4bf12
Step 2/7 : ENV NGINX_VERSION 1.13.6-1~stretch
 ---> Using cache
 ---> 4e9431a424a0
 ......
Step 6/7 : EXPOSE 80 443 8080
 ---> Running in 0946f23dfa0d
Removing intermediate container 0946f23dfa0d
 ---> 4c9621ff1142
Step 7/7 : CMD ["nginx", "-g", "daemon off;"]
 ---> Running in 9a88247bd1a3
Removing intermediate container 9a88247bd1a3
 ---> 837797137461
Successfully built 837797137461
Successfully tagged customname:latest
```
3. To choose some other Dockerfile, use `-f` option
```bash
$ docker image build -t customname -f some-dockerfile .
```
4. Docker image history will look like this
```bash
$ docker image history customname
IMAGE               CREATED             CREATED BY                                      SIZE                COMMENT
837797137461        2 minutes ago       /bin/sh -c #(nop)  CMD ["nginx" "-g" "daemon…   0B                  
4c9621ff1142        2 minutes ago       /bin/sh -c #(nop)  EXPOSE 443 80 8080           0B                  
8d433ab6eefd        4 minutes ago       /bin/sh -c ln -sf /dev/stdout /var/log/nginx…   22B                 
f2b0cd8641fd        4 minutes ago       /bin/sh -c apt-get update  && apt-get instal…   73.4MB              
3a786aa7fb59        29 minutes ago      /bin/sh -c #(nop)  ENV NJS_VERSION=1.13.6.0.…   0B                  
4e9431a424a0        29 minutes ago      /bin/sh -c #(nop)  ENV NGINX_VERSION=1.13.6-…   0B                  
054366b4bf12        3 days ago          /bin/sh -c #(nop)  CMD ["bash"]                 0B                  
<missing>           3 days ago          /bin/sh -c #(nop) ADD file:40f52c233aecabf57…   55.3MB
```

5. Clean up after yourself
