docker run -p 4040 -p 5050 -w /work -v $(pwd):/work maven:3.6-jdk-11 mvn clean verify package
