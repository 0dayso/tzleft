#/bin/bash

selfpath=$(cd "$(dirname "$0")"; pwd)
tops=$(cd "$(dirname "$selfpath/../../..")"; pwd)
bin=$tops/script/auto_deploy.py

$bin -t $tops -s /usr/local/tools/jpecker-tomcat -w tops-farerule-jpecker-server -p tops-farerule/tops-farerule-jpecker-server -r test --host 192.168.164.222 -u root -a Abc12345
