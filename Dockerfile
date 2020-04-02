FROM maven:3.6.3-jdk-8
LABEL maintainer=dan.napierski@toptal.com

WORKDIR /aida/nlp-util/
COPY . .
RUN mvn install

CMD [ "/bin/bash", "" ]
