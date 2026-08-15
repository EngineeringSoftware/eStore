FROM eclipse-temurin:17.0.9_9-jdk

WORKDIR /

# Install MAVEN and download the dataset for LDBC SNB SF 0.1
RUN apt-get update -y &&\
    apt-get install -y zstd &&\
    wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar &&\
    wget --no-check-certificate https://repository.surfsara.nl/datasets/cwi/snb/files/social_network-csv_composite-longdateformatter/social_network-csv_composite-longdateformatter-sf1.tar.zst &&\
    zstd -d social_network-csv_composite-longdateformatter-sf1.tar.zst &&\
    tar -xvf social_network-csv_composite-longdateformatter-sf1.tar && rm *.tar

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
COPY --chown=$username ./images/execute.sh /home/$username/estore/ldbc/snb/execute.sh
COPY --chown=$username ./libs /home/libs

WORKDIR /home/$username/estore/ldbc/snb

# Run the test
CMD ["bash","execute.sh"]
