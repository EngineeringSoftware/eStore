FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
        openjdk-8-jdk maven wget zstd netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /estore
COPY . .

RUN ./s install_estore

EXPOSE 1234
CMD ["/bin/bash"]
