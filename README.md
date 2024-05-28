## CS 122B Project 5 Murphy movies example

1. This example application allows you to log in, see a movie by Eddie Murphy and leave comments.
2. In branch `Dockerized`, you will see how this application is turned into a docker image and deployed to AWS using Docker
3. In branch `Kubernetes-Compatible`, you will see how this application is modified to be deployed using Kubernetes pods

### This README.md file is used for the main branch
### Before running the example

#### We need to create a user that is allowed to login from anywhere

- Login to MySQL as a `root` user
   ```
   mysql -u root -p
   ```

- Create an account called `murphyuser` and grant privileges. Note that `@'%'` allows the user to login from anywhere instead of only `@'localhost'`. This command is critical for Docker and Kubernetes:
   ```
   CREATE USER 'murphyuser'@'%' IDENTIFIED BY 'My7$Password';
   GRANT ALL PRIVILEGES ON * . * TO 'murphyuser'@'%';
   exit;
   ```

#### Prepare the database `murphymovies`

- Login to MySQL as `murphyuser`:
  
  ```
  mysql -u murphyuser -p
  ```
- Initialize the database

```
CREATE DATABASE IF NOT EXISTS murphymovies;
USE murphymovies;
CREATE TABLE IF NOT EXISTS stars(
      id varchar(10) primary key,
      name varchar(100) not null,
      birthYear integer
);

INSERT IGNORE INTO stars VALUES('755017', 'Eddie Murphy', 1961);

CREATE TABLE if not exists movies(
      id VARCHAR(10) DEFAULT '',
      title VARCHAR(100) DEFAULT '',
      year INTEGER NOT NULL,
      director VARCHAR(100) DEFAULT '',
      PRIMARY KEY (id)
);

INSERT IGNORE INTO movies VALUES('2222', 'Coming To America', 1988, 'John Landis');

CREATE TABLE IF NOT EXISTS stars_in_movies(
      starId VARCHAR(10) DEFAULT '',
      movieId VARCHAR(10) DEFAULT '',
      FOREIGN KEY (starId) REFERENCES stars(id),
      FOREIGN KEY (movieId) REFERENCES movies(id)
);

INSERT IGNORE INTO stars_in_movies VALUES('755017', '2222');

CREATE TABLE IF NOT EXISTS comments(
      comment TEXT NOT NULL
);

INSERT INTO comments VALUE('Good');

```

### Explanation of the Tomcat application

- The default login username for the application is `anteater`, and the password is `123456`.

- After logging in, you will see Eddie Murphy's info and can leave comments.

- All comments are saved to MySQL.


### Data Sources

- The file `WebContent/META-INF/context.xml` contains two data sources. In the `master` branch, the two data sources  use the same local MySQL server.
- The file `WEB-INF/web.xml` registers the data sources using the names `jdbc/MySQLReadWrite` and `jdbc/MySQLReadOnly`.

- The file `SingleStarServlet.java` uses the `jdbc/MySQLReadOnly` data source to get Eddie Murphy's info.
- The file `CommentServelt.java` uses the `jdbc/MySQLReadWrite` data source to insert and retrieve comments.
