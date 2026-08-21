FROM eclipse-temurin:8-jdk

RUN apt-get update && apt-get install -y --no-install-recommends \
        maven wget zstd netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /estore
COPY . .

RUN ./s install_estore

EXPOSE 1234
CMD ["/bin/bash"]
