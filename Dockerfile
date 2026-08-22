FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

# Java 8 first. Installing maven in the same apt command pulls OpenJDK 11.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        openjdk-8-jdk wget zstd netcat-openbsd \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /estore
COPY . .

RUN ./s install_estore

EXPOSE 1234
CMD ["/bin/bash"]
