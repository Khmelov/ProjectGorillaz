FROM tomcat:10.1.36-jre21-temurin
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"
EXPOSE 8080
