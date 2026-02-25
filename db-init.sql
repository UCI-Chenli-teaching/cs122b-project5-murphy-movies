CREATE USER IF NOT EXISTS 'murphyuser'@'%' IDENTIFIED BY 'My7$Password';
GRANT ALL PRIVILEGES ON *.* TO 'murphyuser'@'%';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS murphymovies;
USE murphymovies;

CREATE TABLE IF NOT EXISTS stars(
      id varchar(10) primary key,
      name varchar(100) not null,
      birthYear integer
);

INSERT IGNORE INTO stars VALUES('755017', 'Eddie Murphy', 1961);

CREATE TABLE IF NOT EXISTS movies(
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

INSERT INTO comments VALUES('Good');
