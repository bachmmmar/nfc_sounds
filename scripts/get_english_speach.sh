#!/bin/bash

TARGET_DIR="../app/src/main/assets/nfc_name"

BASE_URL="https://dictionary.cambridge.org"
SEARCH_POSTFIX="/de/worterbuch/englisch/"
KEY_WORD='data-src-mp3="/de/media/englisch/us_'


WORD=$1


wget -O tmp.html ${BASE_URL}${SEARCH_POSTFIX}${WORD} > /dev/null 2>&1

LINE_WITH_URL=$(cat tmp.html | grep -m1 $KEY_WORD)

AUDIO_POSTFIX=$(echo $LINE_WITH_URL | sed -e 's/.*data-src-mp3="//g' -e 's/" data-src-ogg=.*//g')


DESTINATION_DIR=${TARGET_DIR}/${WORD}/en_us/
DESTINATION=$DESTINATION_DIR${WORD}.mp3
SOURCE=$BASE_URL$AUDIO_POSTFIX
mkdir -p $DESTINATION_DIR

echo "saving " $SOURCE " to " $DESTINATION
wget -O $DESTINATION $SOURCE > /dev/null

