FROM eclipse-temurin:17.0.9_9-jdk

WORKDIR /

# Install MAVEN and download the dataset for LDBC SNB SF 0.1
RUN apt-get update -y &&\
    apt-get install -y zstd &&\
    apt-get install -y unzip &&\
    wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar &&\
    wget -O social_network-csv_composite-longdateformatter-sf10.tar.gz -L https://utexas.box.com/shared/static/nc2a2ur4ugmddj4jqfmx3nesj9umsv4u.gz &&\
    gzip -d social_network-csv_composite-longdateformatter-sf10.tar.gz &&\
    tar -xvf social_network-csv_composite-longdateformatter-sf10.tar && rm *.tar &&\
    wget -O neo4j-5.13.0.tar.gz -L https://utexas.box.com/shared/static/ikoe3sl4fatoh5imfkuyw2dcb8q7ieds.gz &&\
    gzip -d neo4j-5.13.0.tar.gz && tar -xvf neo4j-5.13.0.tar

ENV PATH="${PATH}:/apache-maven-3.9.5/bin"

WORKDIR /social_network-csv_composite-longdateformatter-sf10


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
RUN chown -R $username:$groupname /neo4j

USER $username

COPY --chown=$username ./estore/ldbc/snb/src/test/java/org/estore/eval/estore/ldbc/snb/Neo4jImpermanantTest10 /neo4j/community/community-it/kernel-it/src/test/java/org/neo4j/kernel/impl/core/Neo4jImpermanantTest10.java

COPY --chown=$username ./images/execute.sh /neo4j/community/community-it/kernel-it/execute.sh
COPY --chown=$username ./estore/ldbc/snb/src/test/java/org/estore/eval/estore/ldbc/snb/modified_pom /neo4j/community/community-it/kernel-it/pom.xml
COPY --chown=$username ./estore/ldbc/snb/src/test/java/org/estore/eval/estore/ldbc/snb/modified_main_pom /neo4j/pom.xml


WORKDIR /neo4j


RUN mvn install -DskipTests -Dlicense.skip=true -Dcheckstyle.skip -Drat.skip=true

WORKDIR /neo4j/community/community-it/kernel-it

# Run the test
CMD ["bash","execute.sh"]
