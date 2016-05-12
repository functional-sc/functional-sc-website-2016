#!/bin/sh

rm target/*.war

lein ring uberwar

cp target/*.war ../functional-sc-appserver/webapps/ROOT.war

echo "deployed to ../functional-sc-appserver/webapps/ROOT.war"