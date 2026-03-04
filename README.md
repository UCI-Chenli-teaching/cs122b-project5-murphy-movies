# CS 122B Project 5 Murphy movies example

1. This example application allows you to log in, see a movie by Eddie Murphy and leave comments.
2. In branch `Docker`, you will see how this application is turned into a docker image and deployed to AWS using Docker
3. In branch `Kubernetes`, you will see how this application is modified to be deployed using Kubernetes pods
4. In branch `Multi-Service`, you will see how this application is modified to a multi-service architecture.

## This README.md file is used for the Multi-Service branch

## Brief Explanation

- We modified the `pom.xml` to compile different parts of the project into different WAR files.
- We use Redis for session state and star pod state, so this setup does not rely on sticky sessions or `HttpSession`.

### Storing States

There are two kinds of state in this application:

1. **Login session state** is stored in Redis under `session:<sessionId>`.
   - `login/LoginServlet.java` creates a session object (`username`, `loginTime`), serializes it as JSON, and stores it in Redis using `set`.
   - `common/LoginFilter.java` reads `redisSessionId` from cookie, loads the serialized session JSON using `get`, deserializes it, and attaches `username` and `loginTime` to request attributes.

2. **`accessCount` (star-service-only state)** is stored in Redis under `accessCount:<username>`.
   - `star/SingleStarServlet.java` increments this key with `incr` for each request.
   - Since Redis is shared by all star pods, the count stays consistent across pods.

### Redis Configuration

Redis configuration is in `WebContent/META-INF/context.xml`:

```xml
<Environment name="redis/Address" type="java.lang.String" value="redis-service:6379"/>
```

Tomcat has first-class JNDI support for JDBC `DataSource` resources (MySQL), but not a built-in Jedis pool resource factory. So this example uses a JNDI environment entry for Redis address (`host:port`) and initializes the Jedis pool in `common/RedisUtil.java`.

### Maven Profiles
The original Maven configuration will compile everything in the codebase into a war file. Using Maven Profiles, we can compile different part of project into different war files.
In this branch, we split `/api/login` endpoint to a login profile, and the other endpoints to a star profile.
- First, split the source files into different packages. Note that we have a package called `common`, which is shared among different profiles.
- Next, modify the `pom.xml` to set up different profiles. 
  - Line 46: we change the sourceDirectory from `src` to a parameter `${endpointDir}`. You can set different value to this parameter for different profiles. 
  - Line 61: we set a parameter `${excludes}`, which can be used to exclude some static files inside `WebContent`.
  - Line 64-81 show how to add common package to all profiles
  - Line 85-107 show how to use Profiles. For each profile we define the value of `endpointDir` and `excludes`.

The `Dockerfile` is also updated. At line 7 we defined an argument `MVN_PROFILE`, you can set its value when building an image.

## Build different profiles
- Compile different part of the project into war file with
  ```
  mvn package -P ${profileName}
  ``` 
- Login endpoint: 
  ```
  mvn package -P login
  ```
- Star endpoints:
  ```
  mvn package -P star
  ```

If you see errors in Intellij, open the Maven panel at the right. Expand "Profiles" and select only "default". Then reload the Maven Project.

## Build different Docker images

- Build the image for login endpoint with 
  ```
  sudo docker build . --build-arg MVN_PROFILE=login --platform linux/amd64 -t <DockerHub-user-name>/cs122b-p5-murphy-login:v1
  ```
  - We specify the Maven profile name with `--build-arg MVN_PROFILE=${profileName}`
- Push the image to DockerHub with 
  ```
  sudo docker push <DockerHub-user-name>/cs122b-p5-murphy-login:v1
  ```
- Repeat the steps for star endpoint:
  ```
  sudo docker build . --build-arg MVN_PROFILE=star --platform linux/amd64 -t <DockerHub-user-name>/cs122b-p5-murphy-star:v1
  ```
  ```
  sudo docker push <DockerHub-user-name>/cs122b-p5-murphy-star:v1
  ```

### Clean up
- list all images 
```
sudo docker images
``` 
- delete image
```
sudo docker rmi <image ID>
```
