FROM eclipse-temurin:17.0.9_9-jdk

WORKDIR /

# Install MAVEN and download the dataset for LDBC FinBench SF 0.1
RUN apt-get update -y &&\
    apt-get install -y zstd &&\
    wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar &&\
    wget -O sf3.tar.gz -L https://utexas.box.com/shared/static/ee7obrlyqzj74jtc23lk4vbrbyaf3ml4.gz &&\
    gzip -d sf3.tar.gz &&\
    tar -xvf sf3.tar && rm *.tar
    
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
