FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/usr/java/jdk-25
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Oracle JDK 25 first so apt maven does not become the default java.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates wget zstd netcat-openbsd \
    && ARCH="$(dpkg --print-architecture)" \
    && case "$ARCH" in \
         amd64) JDK_ARCH=x64 ;; \
         arm64) JDK_ARCH=aarch64 ;; \
         *) echo "unsupported arch: $ARCH" && exit 1 ;; \
       esac \
    && wget -q "https://download.oracle.com/java/25/latest/jdk-25_linux-${JDK_ARCH}_bin.tar.gz" -O /tmp/jdk.tgz \
    && echo "$(wget -qO- "https://download.oracle.com/java/25/latest/jdk-25_linux-${JDK_ARCH}_bin.tar.gz.sha256") */tmp/jdk.tgz" | sha256sum -c \
    && mkdir -p "$JAVA_HOME" \
    && tar --extract --file /tmp/jdk.tgz --directory "$JAVA_HOME" --strip-components 1 \
    && rm /tmp/jdk.tgz \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /estore
COPY . .

RUN ./s install_estore

EXPOSE 1234
CMD ["/bin/bash"]
