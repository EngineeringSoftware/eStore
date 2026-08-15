FROM jkenis/memgraph:finbench-0.1

WORKDIR /

USER root

# Install MAVEN and download the dataset for LDBC SNB SF 0.1
RUN apt-get -y update && apt-get -y install wget openjdk-17-jdk &&\
    wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar
    

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
RUN chown -R $username:$groupname /var &&\
    chown -R $username:$groupname /memgraph

USER $username

COPY --chown=$username ./pom.xml /home/$username/pom.xml
COPY --chown=$username ./estore /home/$username/estore
COPY --chown=$username ./images/execute.sh /home/$username/estore/ldbc/finbench/execute.sh
COPY --chown=$username ./libs /home/libs

WORKDIR /home/$username/estore/ldbc/finbench


# Run the test
ENTRYPOINT ["/usr/bin/env"]
CMD ["bash","execute.sh"]
    

