#!/bin/bash

TMP_FILE=/tmp/insert-output.txt

if [ -e $TMP_FILE ]; then
	rm $TMP_FILE
fi

n=0
while IFS= read line; do
  if [[ "$line" =~ $1 && $n = 0 ]]; then
    echo -e "\t<!-- INSERTED BY DOCKER BUILD SCRIPT -->\n" >> $TMP_FILE
    cat $2 >> $TMP_FILE
    n=1
  fi
  echo "$line" >> $TMP_FILE
done < $3

mv $TMP_FILE $3
