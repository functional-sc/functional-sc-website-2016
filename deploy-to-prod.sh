#!/bin/sh

echo "building..."
rm target/*.war 
lein ring uberwar

echo "deploying to ../functional-sc-appserver/webapps/ROOT.war"
cp target/*.war ../functional-sc-appserver/webapps/ROOT.war

echo "deploying to fpsc@fpsc Digital Ocean, old war is /tmp/old-ROOT.war"
ssh fpsc@fpsc "cp ~/jetty/webapps/ROOT.war /tmp/old-ROOT.war"
scp target/*.war fpsc@fpsc:~/jetty/webapps/ROOT.war
