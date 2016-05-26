#!/bin/sh

rm target/*.war

lein ring uberwar

cp target/*.war ../functional-sc-appserver/webapps/ROOT.war
echo "deployed to ../functional-sc-appserver/webapps/ROOT.war"

scp target/*.war fpsc@do:~/jetty/webapps/ROOT.war
echo "deployed digital ocean"
