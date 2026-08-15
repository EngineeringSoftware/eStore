FROM auditt/neo4j-5.13.0-profile-print:v1

WORKDIR /

# Install MAVEN and download the dataset for LDBC SNB SF 100
RUN wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar &&\
    wget -O social_network-csv_composite-longdateformatter-sf100.tar.gz -L <INSERT UT BOX URL> &&\
    gzip -d social_network-csv_composite-longdateformatter-sf100.tar.gz &&\
    tar -xvf social_network-csv_composite-longdateformatter-sf100.tar && rm *.tar

ENV PATH="${PATH}:/apache-maven-3.9.5/bin"

WORKDIR /social_network-csv_composite-longdateformatter-sf100


# Import the dataset into neo4j server and start it
RUN neo4j-admin database import full \
    --id-type=INTEGER \
    --ignore-empty-strings=true \
    --bad-tolerance=0 \
    --nodes=Place="./static/place_0_0.csv" \
    --nodes=Organisation="./static/organisation_0_0.csv" \
    --nodes=TagClass="./static/tagclass_0_0.csv" \
    --nodes=Tag="./static/tag_0_0.csv" \
    --nodes=Forum="./dynamic/forum_0_0.csv" \
    --nodes=Person="./dynamic/person_0_0.csv" \
    --nodes=Comment="./dynamic/comment_0_0.csv" \
    --nodes=Post="./dynamic/post_0_0.csv" \
    --relationships=IS_PART_OF="./static/place_isPartOf_place_0_0.csv" \
    --relationships=IS_LOCATED_IN="./dynamic/person_isLocatedIn_place_0_0.csv" \
    --relationships=HAS_TYPE="./static/tag_hasType_tagclass_0_0.csv" \
    --relationships=HAS_CREATOR="./dynamic/comment_hasCreator_person_0_0.csv" \
    --relationships=IS_LOCATED_IN="./dynamic/comment_isLocatedIn_place_0_0.csv" \
    --relationships=REPLY_OF="./dynamic/comment_replyOf_comment_0_0.csv" \
    --relationships=REPLY_OF="./dynamic/comment_replyOf_post_0_0.csv" \
    --relationships=CONTAINER_OF="./dynamic/forum_containerOf_post_0_0.csv" \
    --relationships=HAS_MEMBER="./dynamic/forum_hasMember_person_0_0.csv" \
    --relationships=HAS_MODERATOR="./dynamic/forum_hasModerator_person_0_0.csv" \
    --relationships=HAS_TAG="./dynamic/forum_hasTag_tag_0_0.csv" \
    --relationships=HAS_INTEREST="./dynamic/person_hasInterest_tag_0_0.csv" \
    --relationships=KNOWS="./dynamic/person_knows_person_0_0.csv" \
    --relationships=LIKES="./dynamic/person_likes_comment_0_0.csv" \
    --relationships=LIKES="./dynamic/person_likes_post_0_0.csv" \
    --relationships=HAS_CREATOR="./dynamic/post_hasCreator_person_0_0.csv" \
    --relationships=HAS_TAG="./dynamic/comment_hasTag_tag_0_0.csv" \
    --relationships=HAS_TAG="./dynamic/post_hasTag_tag_0_0.csv" \
    --relationships=IS_LOCATED_IN="./dynamic/post_isLocatedIn_place_0_0.csv" \
    --relationships=STUDY_AT="./dynamic/person_studyAt_organisation_0_0.csv" \
    --relationships=WORK_AT="./dynamic/person_workAt_organisation_0_0.csv" \
    --relationships=IS_LOCATED_IN="./static/organisation_isLocatedIn_place_0_0.csv" \
    --relationships=IS_SUBCLASS_OF="./static/tagclass_isSubclassOf_tagclass_0_0.csv" \
    --delimiter '|' &&\
    neo4j-admin dbms set-initial-password passwd123


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
RUN chown -R $username:$groupname /neo4j-5.13.0

USER $username

COPY --chown=$username ./pom.xml /home/$username/pom.xml
COPY --chown=$username ./estore /home/$username/estore
COPY --chown=$username ./images/execute.sh /home/$username/estore/ldbc/snb/execute.sh
COPY --chown=$username ./libs /home/libs

WORKDIR /home/$username/estore/ldbc/snb

# Run the test
CMD ["bash","execute.sh"]
    

