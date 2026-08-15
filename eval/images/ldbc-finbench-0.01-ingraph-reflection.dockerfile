FROM eclipse-temurin:17.0.9_9-jdk

WORKDIR /

# Install MAVEN and download the dataset for LDBC FinBench SF 0.01
RUN apt-get update -y &&\
    apt-get install -y zstd &&\
    wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar &&\
    wget -O test.tar.gz -L "https://drive.usercontent.google.com/download?id=1kBouy5zrUE4h9QmklaIiWNPDfC9-XgD-&export=download&authuser=0&confirm=t&uuid=971b21ed-db6a-4c84-b4d1-db510b9ea4f8&at=APZUnTWZ702p54DH7frTbxTgQijv:1701486595409" &&\
    gzip -d test.tar.gz &&\
    tar -xvf test.tar && rm *.tar

ENV PATH="${PATH}:/apache-maven-3.9.5/bin"

ARG username
ARG uid
ARG groupname
ARG gid
RUN groupadd -g $gid $groupname
RUN \
    useradd -m -s /bin/bash -c "$username's clone" -u $uid -g $gid $username && \
    adduser $username sudo && \
    usermod -aG sudo $username && \
    echo "$username:docker" | chpasswd

USER $username

COPY --chown=$username ./pom.xml /home/$username/pom.xml
COPY --chown=$username ./estore /home/$username/estore
COPY --chown=$username ./images/execute.sh /home/$username/estore/ldbc/finbench/execute.sh
COPY --chown=$username ./libs /home/libs

WORKDIR /home/$username/estore/ldbc/finbench

# Run the test
CMD ["bash","execute.sh"]
