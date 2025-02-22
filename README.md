# docker-learn
编写一个Java项目学习Docker使用。

创建`Dockerfile`。`Dockerfile` 是一个 **用于定义 Docker 镜像** 的脚本，它包含了一系列**指令**，告诉 Docker **如何构建**一个自定义的镜像。你可以使用 `Dockerfile` 来 **自动化** 构建镜像，而不是手动安装和配置环境。

```dockerfile
# 使用 Java 8 运行时环境 docker pull eclipse-temurin:8-jre-alpine
FROM eclipse-temurin:8-jre-alpine

# 设置容器中的工作目录
WORKDIR /docker-learn

# 复制 JAR 文件到设置的容器目录中
COPY target/docker-learn.jar app.jar

# 运行 Java 应用
CMD ["java", "-jar", "app.jar"]
```

然后进入应用的根目录下，构建docker镜像

```sh
# 格式。如果不使用标签，默认是latest
docker build -t <镜像名>:<标签> <构建上下文>

# -t指定镜像名。.表示当前目录(目录应是Dockerfile所在目录)
docker build -t docker-learn .
```

> 前提是`docker-learn.jar`已经在`target`路径下

运行镜像，创建一个镜像实例(即容器)

```sh
docker run --rm -p 808:8080 -v D:/Desktop/config.properties:/docker-learn/config.properties docker-learn
```

`docker run [OPTIONS] IMAGE [COMMAND] [ARG...]`表示创建一个容器，常用OPTIONS说明如下

> 更全面的命令使用方式，直接`docker run --help`查阅

* `-d`表示后台运行
* `-p 808:8080`表示将主机的808端口映射到8080端口
* `--name`设置容器名
* `--rm`容器运行完后自动删除
* `-v /test:/app/test`将主机的/test目录挂载到容器的/app/test目录，除了目录也可以是文件
* `-e "http_proxy=host"`设置环境变量

`docker start 容器名`表示启动一个已有容器，不能修改固有的设置。

`docker stop 容器名`表示停止一个已有容器。

导出/加载/拉取镜像。

```sh
# 查看本地镜像
docker images
# 导出本地镜像
docker save -o myimage.tar 镜像名:标签
# 加载镜像
docker load -i /root/myimage.tar
# 拉取镜像
docker pull 镜像名[:标签]
```

如果容器已经在运行，可以进行交互式命令行。

``` sh
docker exec -it 容器id sh
```

