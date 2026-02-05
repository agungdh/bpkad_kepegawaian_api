#!/bin/bash
./mvnw flyway:clean -Dflyway.cleanDisabled=false && ./mvnw flyway:migrate
