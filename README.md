# CS 122B Project 5 Murphy movies example

1. This example application allows you to log in, see a movie by Eddie Murphy and leave comments.
2. In branch `Docker`, you will see how this application is turned into a docker image and deployed to AWS using Docker
3. In branch `Kubernetes`, you will see how this application is modified to be deployed using Kubernetes pods
4. In branch `Multi-Service`, you will see how this application is modified to a multi-service architecture.

## This README.md file is used for the Kubernetes branch

## Brief Explanation

- The Kubernetes branch extends the Docker branch to enable this example application to run in a kubernetes pod while the mysql servers are also running in pods
- There is only one change compared to the Docker branch: a modified context.xml

### context.xml

In a Kubernetes cluster, the MySQL Master/Slave pods will be set up by `helm`. Therefore, the dataSources specified in the [context.xml](/WebContent/META-INF/context.xml) will no longer be individual MySQL servers but rather two Kubernetes services:

- `mysql-primary:3306` will redirect queries to the only Master pod. It can handle both Read and Write queries.
- `mysql-secondary:3306` will redirect queries to one of many Slave pods. It can handle Read queries ONLY.

## Running this example

- Build the image by running 
```
sudo docker build . --platform linux/amd64 -t <DockerHub-user-name>/cs122b-p5-murphy:v2
```
- Note that this image is tagged `v2` to differentiate from the image we built in the `Docker` branch
- push the image to DockerHub with 
```
sudo docker push <DockerHub-user-name>/cs122b-p5-murphy:v2
```

Once the image is pushed to DockerHub, your can refer to the Kubernetes Instruction repo to see how this image is used in the `.yaml` file to "boot up" the pods.

### Clean up
- list all images 
```
sudo docker images
``` 
- delete image
```
sudo docker rmi <image ID>
```
