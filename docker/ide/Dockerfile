FROM orangeopensource/docker-compose-devenv:latest

USER root

# tools
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -qq -y --fix-missing --no-install-recommends \
    jq \
    uuid \
    mariadb-client \
    fakeroot \
    tmux \
    python \
    socat \
    build-essential \
    chrpath \
    libssl-dev \
    libxft-dev \
    libfreetype6 \
    libfreetype6-dev \
    libfontconfig1 \
    libfontconfig1-dev \
    java-package \
    wget \
    gpg-agent \
    systemd \
    gdebi-core \
    openjdk-17-jdk \
    zip

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# install chrome
RUN curl -fSL https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb -o /tmp/google-chrome-stable_current_amd64.deb \
    && gdebi --non-interactive /tmp/google-chrome-stable_current_amd64.deb

USER developer

# maven 3.8 (required for java 17)
ARG MAVEN_VERSION="3.8.6"
RUN curl -s "https://get.sdkman.io" | bash
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
    yes | sdk install maven $MAVEN_VERSION && \
    rm -rf $HOME/.sdkman/archives/* && \
    rm -rf $HOME/.sdkman/tmp/*"

COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["/bin/bash"]