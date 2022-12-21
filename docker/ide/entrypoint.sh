#!/bin/bash

sudo chown -R developer:developer /home/developer/.m2 /home/developer/.java /home/developer/.IntelliJIdea

exec $@

