FROM auditt/neo4j-5.13.0-profile-print:v2
#FROM neo4j:5.13.0-community

WORKDIR /

# Install MAVEN and download the dataset for LDBC SNB SF 0.1
RUN wget -O maven.tar.gz -L https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz &&\
    gzip -d maven.tar.gz && tar -xvf maven.tar && rm *.tar &&\
    wget -O sf0.01.tar.gz -L https://utexas.box.com/shared/static/1ustwk58q1ch95injnvcoyp2g9lxr0ek.gz &&\
    gzip -d sf0.01.tar.gz &&\
    tar -xvf sf0.01.tar && rm *.tar

ENV PATH="${PATH}:/apache-maven-3.9.5/bin"

WORKDIR /sf0.01


# Import the dataset into neo4j server and start it
RUN neo4j-admin database import full \
    --id-type=STRING \
    --ignore-empty-strings=true \
    --bad-tolerance=0 \
    --nodes=Account="./snapshot/Account.csv" \
    --nodes=Company="./snapshot/Company.csv" \
    --nodes=Loan="./snapshot/Loan.csv" \
    --nodes=Medium="./snapshot/Medium.csv" \
    --nodes=Person="./snapshot/Person.csv" \
    --relationships=Repay="./snapshot/AccountRepayLoan.csv" \
    --relationships=Transfer="./snapshot/AccountTransferAccount.csv" \
    --relationships=Withdraw="./snapshot/AccountWithdrawAccount.csv" \
    --relationships=Apply="./snapshot/CompanyApplyLoan.csv" \
    --relationships=Guarantee="./snapshot/CompanyGuaranteeCompany.csv" \
    --relationships=Invest="./snapshot/CompanyInvestCompany.csv" \
    --relationships=Own="./snapshot/CompanyOwnAccount.csv" \
    --relationships=Deposit="./snapshot/LoanDepositAccount.csv" \
    --relationships=SignIn="./snapshot/MediumSignInAccount.csv" \
    --relationships=Apply="./snapshot/PersonApplyLoan.csv" \
    --relationships=Guarantee="./snapshot/PersonGuaranteePerson.csv" \
    --relationships=Invest="./snapshot/PersonInvestCompany.csv" \
    --relationships=Own="./snapshot/PersonOwnAccount.csv" \
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
COPY --chown=$username ./images/execute.sh /home/$username/estore/ldbc/finbench/execute.sh
COPY --chown=$username ./libs /home/libs

WORKDIR /home/$username/estore/ldbc/finbench

# Run the test
CMD ["bash","execute.sh"]
    

