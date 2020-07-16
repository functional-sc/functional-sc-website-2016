#!/bin/sh

echo "building..."
rm target/*.war 
lein ring uberwar

echo "deploying to ../functional-sc-appserver/webapps/ROOT.war"
cp target/*.war ../functional-sc-appserver/webapps/ROOT.war

echo "local deployment complete"


