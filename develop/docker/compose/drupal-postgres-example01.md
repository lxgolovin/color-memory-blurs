# Drupal and postgres example 1

Got one file: `docker-compose.yml`. Here is the listing:
```yaml
# docker-compose.yml file
version: '2'

services:
  drupal:
    image: drupal
    ports:
      - '8080:80'
    volumes:
      - drupal-modules:/var/www/html/modules
      - drupal-profiles:/var/www/html/profiles       
      - drupal-sites:/var/www/html/sites      
      - drupal-themes:/var/www/html/themes
  postgres:
    image: postgres:9.6
    environment:
      - POSTGRES_PASSWORD=some_password

volumes:
  drupal-modules:
  drupal-profiles:
  drupal-sites:
  drupal-themes:
```
