## CS 122B Project 5 Murphy movies example

1. This example application allows you to log in and see a movie by Eddie Murphy.
2. In branch `Dockerized`, you will see how this application is turned into a docker image and deployed to AWS using Docker
3. In branch `Kubernetes-Compatible`, you will see how this application is modified to be deployed using Kubernetes pods

### This README.md file is used for the main branch
### Before running the example

#### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:

- login to mysql as a root user
   ```
   local> mysql -u root -p
   ```

- create a test user and grant privileges:
   ```
   mysql> CREATE USER 'mytestuser'@'%' IDENTIFIED BY 'My6$Password';
   mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'%';
   mysql> quit;
   ```

#### prepare the database `moviedbexample`


```
local> mysql -u mytestuser -p
mysql> CREATE DATABASE IF NOT EXISTS murphymovies;
mysql> USE murphymovies;
mysql> CREATE TABLE IF NOT EXISTS stars(
               id varchar(10) primary key,
               name varchar(100) not null,
               birthYear integer
           );

mysql> INSERT IGNORE INTO stars VALUES('755017', 'Eddie Murphy', 1961);

mysql> CREATE TABLE if not exists movies(
       	    id VARCHAR(10) DEFAULT '',
       	    title VARCHAR(100) DEFAULT '',
       	    year INTEGER NOT NULL,
       	    director VARCHAR(100) DEFAULT '',
       	    PRIMARY KEY (id)
       );

mysql> INSERT IGNORE INTO movies VALUES('2222', 'Coming To America', 1988, 'John Landis');

mysql> CREATE TABLE IF NOT EXISTS stars_in_movies(
       	    starId VARCHAR(10) DEFAULT '',
       	    movieId VARCHAR(10) DEFAULT '',
       	    FOREIGN KEY (starId) REFERENCES stars(id),
       	    FOREIGN KEY (movieId) REFERENCES movies(id)
       );

mysql> INSERT IGNORE INTO stars_in_movies VALUES('755017', '2222');

mysql> CREATE TABLE IF NOT EXISTS comments(
            comment TEXT NOT NULL
       );

mysql> INSERT INTO comments VALUE('Good');

mysql> quit;
```

### Brief Explanation

- The default username is `anteater` and password is `123456` .

- [login.html](WebContent/login.html) contains the login form. In the `form` tag with `id=login_form`, the action is disabled so that we can implement our own logic with the `submit` event. It also includes jQuery and `login.js`.


- [login.js](WebContent/login.js) is responsible for submitting the form. 
  - The statement `login_form.submit(submitLoginForm)` sets up an event listener for the form `submit` action and binds the action to the `submitLoginForm` function. 
  - The `submitLoginForm` function disables the default form action and sends HTTP POST requests to the backend.
  - The `handleLoginResult` function parses the JSON data that is sent from the backend. If login is successful, 'login.js' redirects to the 'index.html' page. If login fails, it shows appropriate error messages.


- [LoginServlet.java](src/LoginServlet.java) handles the login requests. It contains the following functionalities:
  - It gets the username and password from the parameters.
  - It verifies the username and password.
  - If login succeeds, it puts the `User` object in the session. Then it sends back a JSON response: `{"status": "success", "message": "success"}` .
  - If login fails, the JSON response will be: `{"status": "fail", "message": "incorrect password"}` or `{"status": "fail", "message": "user <username> doesn't exist"}`.
   
 
- [LoginFilter.java](src/LoginFilter.java) is a special `Filter` class. It serves the purpose that for each URL request, if the user is not logged in, then it redirects the user to the `login.html` page. 
   - A `Filter` class intercepts all incoming requests and determines if such requests are allowed against the rules we implement. See more details about `Filter` class [here](http://tutorials.jenkov.com/java-servlets/servlet-filters.html).
   - In `Filter`, all requests will pass through the `doFilter` function.
   - `LoginFilter` first checks if the request is `login.html`, `login.js`, or `api/login`, which are the URL patterns we mapped to `LoginServlet.java` that are allowed to access without login.
   - It then checks if the user has logged in to the current session. If so, it redirects the user to the requested URL and if otherwise,`login.html` .

- [Index.html](WebContent/index.html) is the main HTML file that imports jQuery, Bootstrap, and `index.js`.

- [Index.js](WebContent/index.js) is the main Javascript file that initiates an HTTP GET request to the `SingleStarServlet`. After the response is returned, `index.js` populates the table using the data it gets.

- [SingleStarServlet](src/SingleStarServlet.java) is a Java servlet that talks to the database and get information about one Star and all the movie this Star performed. It returns a list of Movies in the JSON format.


### DataSource

- `WebContent/META-INF/context.xml` contains two DataSources, with database information stored in them. For this branch, they point to the same local MySQL server.
  `WEB-INF/web.xml` registers the DataSources to names jdbc/MySQLWrite and jdbc/MySQLRead, which could be referred to anywhere in the project.

- In `SingleStarServlet.java`, a private DataSource reference dataSource is created with `@Resource` annotation. It is a reference to the DataSource `jdbc/MySQLRead` we registered in `web.xml`

- To use DataSource, you can create a new connection to it by `dataSource.getConnection()`, and you can use the connection as previous examples.


