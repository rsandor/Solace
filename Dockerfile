FROM java:7

# Add the repo to the container
ADD . /solace
WORKDIR /solace

# Install ant and ivy
RUN apt-get update
RUN apt-get -y install ant
RUN apt-get -y install wget
RUN wget http://mirrors.gigenet.com/apache//ant/ivy/2.4.0/apache-ivy-2.4.0-bin-with-deps.tar.gz
RUN tar xzf apache-ivy-2.4.0-bin-with-deps.tar.gz
RUN cp apache-ivy-2.4.0/ivy-2.4.0.jar /usr/share/ant/lib

# Build the game server
RUN ant jar

# Run the game server
CMD java -jar build/jar/solace.jar 4000
