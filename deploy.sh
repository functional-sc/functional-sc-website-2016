#!/bin/sh

rm target/*.war

lein ring uberwar

cp target/*.war ../functional-sc-appserver/webapps/ROOT.war
echo "deployed to ../functional-sc-appserver/webapps/ROOT.war"

ssh fpsc@fpsc "cp ~/jetty/webapps/ROOT.war /tmp/old-ROOT.war"
scp target/*.war fpsc@fpsc:~/jetty/webapps/ROOT.war
echo "deployed digital ocean old war is /tmp/old-ROOT.war"
