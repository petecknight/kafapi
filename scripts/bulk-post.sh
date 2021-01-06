#!/usr/bin/env bash

if [[ "$#" -ne 2 ]]
then
  echo "Usage: bulk-post.sh from to"
  exit 1
fi

for ((i=$1;i<=$2;i++));
do
   key=$i
   value=$(openssl rand -base64 32)

   echo "sending key: $key with value: $value "

   http -j POST localhost:30010/messages key=$key value=$value

done
