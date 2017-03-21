FROM hub.fenxibao.com/base/tomcat:8.5

COPY tomcat/conf/server.xml /usr/local/tomcat/conf/server.xml
COPY target/data-query-server.war /usr/local/tomcat/webapps/queryapi.war

EXPOSE 8080

