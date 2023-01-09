# FROM paketobuildpacks/builder:full
# FROM paketobuildpacks/builder:base
# FROM paketobuildpacks/builder:tiny
# FROM bellsoft/liberica-native-image-kit-container
# FROM bellsoft/liberica-runtime-container:jre
# FROM bellsoft/liberica-openjre-alpine-musl:17
# FROM eclipse-temurin:17-jdk-alpine
# FROM eclipse-temurin:17-jre
# FROM registry.access.redhat.com/ubi8/ubi-minimal:8.7
# FROM ubuntu:20.04
# FROM quay.io/quarkus/quarkus-micro-image:2.0
# FROM gcr.io/paketo-buildpacks/bellsoft-liberica:9.10
# FORM gcr.io/paketo-buildpacks/java-native-image:7.43
# heroku/buildpacks:20 is based on ubuntu:20.04
# FROM heroku/buildpacks:20
FROM ubuntu:20.04

# to fix error : ./application: error while loading shared libraries: libfreetype.so: cannot open shared object file: No such file or directory
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -qq -y --fix-missing --no-install-recommends \
      libfreetype-dev \
      && apt-get autoremove -y \
      && apt-get autoclean -y \
      && apt-get clean -y \
      && rm -rf /usr/share/locale/* \
      && rm -rf /var/cache/debconf/* \
      && rm -rf /var/lib/apt/lists/* \
      && rm -rf /usr/share/doc/*

WORKDIR /work/

COPY target/app-boot-3 /work/application

EXPOSE 8081

CMD ["./application"]
