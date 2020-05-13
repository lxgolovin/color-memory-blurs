# Web + proxy example 2
Got two files. `docker-compose.yml` and `nginx.conf`. Here is the listing:
```yaml
# docker-compose.yml file
version: '2'

services:
  proxy:
    build:
      context: .
      dockerfile: nginx.Dockerfile
    ports:
      - '80:80'
  web:
    image: httpd
    volumes:
        # in the html folder the sources of the web page should be present
      - ./html:/usr/local/apache2/htdocs
```
And `nginx.Dockerfile`
```bash
FROM nginx:1.13

COPY nginx.conf /etc/nginx/conf.d/default.conf
```
Config file of the nginx
```bash
server {
    listen 80;

    location / {
		proxy_pass         http://web;
		proxy_redirect     off;
		proxy_set_header   Host $host;
		proxy_set_header   X-Real-IP $remote_addr;
		proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header   X-Forwarded-Host $server_name;
	}
}
```
