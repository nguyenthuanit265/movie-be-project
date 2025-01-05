jdbc:mysql://localhost:3306/db_spring_security?allowPublicKeyRetrieval=true&useSSL=false

- docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=db_spring_security -d -p 3306:3306 -v mysql-data:/var/lib/mysql mysql

- curl http://localhost:8081/api/v1/public

- curl -u admin:123456 http://localhost:8081/api/v1/admins/profiles

- curl -u wronguser:wrongpass http://localhost:8081/api/v1/users/profiles

- curl -u user:123456 http://localhost:8081/api/v1/admins/profiles