#!/usr/bin/env bash

while( true )

do

   key=$(( ( RANDOM % 100) ))

   echo "getting key: $key"

   http -j GET localhost:30010/messages/$key

done
