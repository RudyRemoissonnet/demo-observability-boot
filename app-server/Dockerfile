# to install other lib
#FROM registry.access.redhat.com/ubi8/ubi-minimal:8.7
#RUN microdnf install freetype fontconfig \
#    && microdnf clean all
FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/app-server /work/application

EXPOSE 7654
USER 1001

CMD ["./application"]
