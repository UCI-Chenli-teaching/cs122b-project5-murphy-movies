# CS 122B Project 5 Murphy movies example

1. This example application allows you to log in, see a movie by Eddie Murphy and leave comments.
2. In branch `Docker`, you will see how this application is turned into a docker image and deployed to AWS using Docker
3. In branch `Kubernetes`, you will see how this application is modified to be deployed using Kubernetes pods
4. In branch `Multi-Service`, you will see how this application is modified to a multi-service architecture.

## This README.md file is used for the Multi-Service branch

## Brief Explanation

- The Multi-Service branch uses Json Web Token(JWT) instead of session to store the user information. 
- We modified the `pom.xml` to compile different part of the projects into different war files.

### Json Web Token(JWT)

We will use JWT to replace session. A utility class `JwtUtil` is added to help you use JWT. 
The changes in `LoginServlet` and `LoginFilter` show you how to replace session with JWT.
- `common/JwtUtil.java`: It contains functions to generate JWT, validate JWT and set JWT into cookies.
- `login/LoginServlet.java` It shows how to generate JWT. Login username is encoded to a JWT string. Then the JWT string is set to cookies, so the later requests will always contain the JWT string.
- `common/LoginFilter.java`: It shows how to get JWT from cookies and how to validate the JWT string. 
- `star/SingleStarServlet.java`: It shows how to get the states stored in JWT token and update the state. We use an accessCount example similar to `cs122b-project2-session-example`, note the difference between session and JWT.

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
  mvn package -P${profileName}
  ``` 
- Login endpoint: 
  ```
  mvn package -Plogin
  ```
- Star endpoints:
  ```
  mvn package -Pstar
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
