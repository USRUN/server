#!/bin/sh
server=root@128.199.168.137
server_dir=/home/usrun

rsync -auvr ./conf $server:$server_dir/
rsync -auvr ./mvnw.cmd $server:$server_dir/
rsync -auvr ./target $server:$server_dir/


