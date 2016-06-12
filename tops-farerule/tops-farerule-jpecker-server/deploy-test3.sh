#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py

$bin -t $tops -s /usr/local/tops-farerule-jpecker-tomcat -w tops-farerule-jpecker-server -p tops-farerule/tops-farerule-jpecker-server -r test3 --host 192.168.161.189 -u root -a Abc12345
