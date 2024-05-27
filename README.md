# CS 122B Project 5 Murphy movies example

1. This example application allows you to log in, see a movie by Eddie Murphy and leave comments.
2. In branch `Dockerized`, you will see how this application is turned into a docker image and deployed to AWS using Docker
3. In branch `Kubernetes-Compatible`, you will see how this application is modified to be deployed using Kubernetes pods

## This README.md file is used for the Dockerized branch

## Brief Explanation

- The Deckerized branch extends the main branch to enable this example application to run in Docker containers.
- There are two changes compared to the main, a new Dockerfile in the root folder and a modified context.xml

### Dockerfile
- This file provides instructions to the docker engine on how to package the whole application into a docker image
- The docker image will be used to "boot up" docker containers, which can be thought of as lightweight "virtual machines" that are dedicated to run your application only

### context.xml
- Since the tomcat server will be running inside Docker containers, from its perspective, the url of the MySQL server changes.
- They used to be running on the same machine (same network) so the MySQL server used to be found at `localhost:3306`
- Now MySQL server will be found at `host.docker.internal:3306`


## Running this example
- We will build a docker image on your local machine, push it to DockerHub, pull it from a AWS machine, and run a Docker container on AWS to serve the website.

### Before running the example
- follow the instructions from the main branch README to set up the `murphymovies` database on an AWS machine
- On the AWS machine, edit the `/etc/mysql/mysql.conf.d/mysqld.cnf` file and set the bind-address to 0.0.0.0  Restart by `sudo service mysql restart`.
- download docker (the latest version) on your local and aws machines
  - install on AWS (Ubuntu): follow https://docs.docker.com/engine/install/ubuntu/
- register a DockerHub account, log in to your account from the command line with `docker login` on both local and aws

### Build the Docker Image on local machine
Run `docker build . --platform linux/amd64 -t <DockerHub-user-name>/cs122b-p5-murphy:v1 ` in the root folder:
- `-t` means giving this image a tag 
- replace `<DockerHub-user-name>` with the username you just registered
- `cs122b-p5-murphy` is the DockerHub repo name, you may change it to whatever, be consistent in below steps if you do.
- `v1` is the tag name. The naming convention is `v1` ..`v2` for incremental version number.
- `--platform linux/amd64` ensures that the image built will be compatible with the CPU architecture of the AWS machines

### After image is built:
- view images with `docker images`, note the image tag and ID
- push the image to DockerHub with `docker push <DockerHub-user-name>/cs122b-p5-murphy:v1`
- Log in your DockerHub web page to see the pushed image, ignore all the image vulnerability warnings
- On the AWS machine, pull the image with `sudo docker pull <DockerHub-user-name>/cs122b-p5-murphy:v1`

### Verify your Docker Image works on a AWS machine
Run `sudo docker run --add-host host.docker.internal:host-gateway -p 8080:8080 <image ID>` to start a docker container to run your application
- `-p 8080:8080` means bind the port 8080 (first) of the host machine to the port 8080 (second) of the container
- So when the host machine (the aws machine) receives a request via port 8080, the request will be relayed to the container's port 8080
- add `-d` before `-p` to enable detached mode so your container runs in the background
- Access the website via `<AWS_PUBLIC_IP>:8080/cs122b-project5-murphy-movies/login.html`.

### Clean up
- `docker ps -a` list all the containers
- `docker rm <container ID>` to delete container
- `docker images` list all images
- `docker rmi <image ID>` to delete image
- add `sudo` if running the commands on an AWS machine or checkout [manager Docker as a non-root user](https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user)