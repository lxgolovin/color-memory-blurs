# Inside Dockerfile
## FROM
All images must have a `FROM`, usually from a minimal Linux distribution like debian or (even better) alpine. If you truly want to start with an empty container, use `FROM scratch`
Example:
```
FROM debian:stretch-slim
```
Better practice is to extend/change an existing official image from Docker Hub. Also highly recommend you always pin versions for anything beyond dev/latest
```
# No this
FROM nginx:latest
# but this
FROM nginx:1.17.10
```
In this case, no need to specify `EXPOSE` or `CMD` in most cases, because they're in my `FROM`

## ENV
`ENV` is an optional environment variable that's used in later lines and set as envvar when container is running
```
ENV NGINX_VERSION 1.13.6-1~stretch
ENV NJS_VERSION   1.13.6.0.1.14-1~stretch
```

## RUN
`RUN` is an optional command to run at shell inside container at build time. The below one adds package repo for nginx from nginx.org and installs it
```
RUN apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y gnupg1 \
	&& \
	NGINX_GPGKEY=573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62; \
	found=''; \
	for server in \
		ha.pool.sks-keyservers.net \
		hkp://keyserver.ubuntu.com:80 \
		hkp://p80.pool.sks-keyservers.net:80 \
		pgp.mit.edu \
	; do \
		echo "Fetching GPG key $NGINX_GPGKEY from $server"; \
		apt-key adv --keyserver "$server" --keyserver-options timeout=10 --recv-keys "$NGINX_GPGKEY" && found=yes && break; \
	done; \
	test -z "$found" && echo >&2 "error: failed to fetch GPG key $NGINX_GPGKEY" && exit 1; \
	apt-get remove --purge -y gnupg1 && apt-get -y --purge autoremove && rm -rf /var/lib/apt/lists/* \
	&& echo "deb http://nginx.org/packages/mainline/debian/ stretch nginx" >> /etc/apt/sources.list \
	&& apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y \
						nginx=${NGINX_VERSION} \
						nginx-module-xslt=${NGINX_VERSION} \
						nginx-module-geoip=${NGINX_VERSION} \
						nginx-module-image-filter=${NGINX_VERSION} \
						nginx-module-njs=${NJS_VERSION} \
						gettext-base \
	&& rm -rf /var/lib/apt/lists/*
```

To forward request and error logs to docker log collector use this command:
```
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
	&& ln -sf /dev/stderr /var/log/nginx/error.log
```

## EXPOSE
To expose ports on the docker virtual network `EXPOSE` is used. But you still need to use -p or -P to open/forward these ports on host
```
EXPOSE 80 443
```

## CMD
`CMD` is required: run this command when container is launched. Only one CMD allowed, so if there are multiple, last one wins
```
CMD ["nginx", "-g", "daemon off;"]
```

> NOTE: better to keep most changed values at the bottom of the file. Less changed values on the top. The reason is that during rebuild image it could be monitored better and same much time during this step

## WORKDIR
Is used to change working directory to root of the container. Using `WORKDIR` is preferred to using `RUN cd /some/path`
```
WORKDIR /usr/share/nginx/html
```
## COPY
`COPY` is used to copy file from file system to container
```
COPY index.html index.html
```
## LABEL
The `LABEL` instruction adds metadata to an image. A LABEL is a key-value pair. To include spaces within a `LABEL` value, use quotes and backslashes as you would in command-line parsing. A few usage examples:
```
LABEL "com.example.vendor"="ACME Incorporated"
LABEL com.example.label-with-value="foo"
LABEL version="1.0"
LABEL description="This text illustrates \
that label-values can span multiple lines."
```
## STOPSIGNAL
The `STOPSIGNAL` instruction sets the system call signal that will be sent to the container to exit. This signal can be a valid unsigned number that matches a position in the kernel’s syscall table, for instance 9, or a signal name in the format `SIGNAME`, for instance `SIGKILL`.
```
STOPSIGNAL SIGTERM
```

-------------------------------------------------------------

> NOTE: this example is taken from the default Dockerfile for the official nginx Docker Hub Repo
> https://hub.docker.com/_/nginx/
