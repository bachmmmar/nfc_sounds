#!/bin/bash

ASSETS_PATH=../app/src/main/assets/nfc_name
MAXSIZE=500000

# width x height
RESIZE_SIZE=900x1500

for f in $(find $ASSETS_PATH * | grep -iE '.*(.jpg|.png)'); do
    before=$(stat -f '%z' $f)
    if [ $before -ge $MAXSIZE ]; then
	
	if [ -f $f~ ]; then
	    echo "$f is larger (${before}) than allowed. but has allready been resized. so it will be skipped!"
	else
	    mogrify -resize $RESIZE_SIZE $f
	    touch $f~
	    after=$(stat -f '%z' $f)
       
	    echo "$f is larger (${before}) than allowed. size was reduced to $after" 
	fi
    fi
    
done
