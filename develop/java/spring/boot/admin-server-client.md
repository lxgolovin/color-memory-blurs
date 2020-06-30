# Spring Boot Admin

Monitor your application with admin web page

## Server Side

* add dependency:
```
implementation 'de.codecentric:spring-boot-admin-starter-server'
```

* enable server with `@EnableAdminServer`
```
@SpringBootApplication
@EnableAdminServer
public class AdminServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdminServerApplication.class, args);
	}
}
```

* set up port in properties file (default is 8080)
```
server.port=8888
```

## Client Side

* add dependencies:
```
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'de.codecentric:spring-boot-admin-starter-client:2.2.3'
```

* define server in properties file
```
spring.boot.admin.client.url=http://localhost:8888
```

* add actuator REST
```
management.endpoints.web.exposure.include=*
```
