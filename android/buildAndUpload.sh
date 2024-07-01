#!/bin/bash

if [[ "$1" != "alpha" ]] && [[ "$1" != "release" ]] && [[ "$1" != "" ]]
then
  echo "Usage: buildAndUpload [alpha|release]"
  exit 1
fi

TOKEN="xoxb-59098123589-607122139957-1Gsvr16D4Czj855H16fkTlLv"
CHANNEL="#general"

# Build APKs

if [[ "$1" = "alpha" ]] || [[ "$1" = "" ]]
then
  echo "Building Alphas..."
  ./gradlew clean
  ./gradlew assembleAlphastagingRelease
  ./gradlew assembleAlphaproductionRelease

  # Upload to Slack
  echo "Uploading to Slack..."
  for filename in mobile/build/outputs/apk/*/*/*.apk; do
    curl -F file=@$filename -F content=@$filename -F channels=$CHANNEL -F token=$TOKEN https://slack.com/api/files.upload
  done

fi

if [[ "$1" = "release" ]] || [[ "$1" = "" ]]
then
  echo "Building Release..."
  ./gradlew clean
  ./gradlew assembleOriginalRelease

  # Upload to Slack
  echo "Uploading to Slack..."
  for filename in mobile/build/outputs/apk/*/*/*.apk; do
    curl -F file=@$filename -F content=@$filename -F channels=$CHANNEL -F token=$TOKEN https://slack.com/api/files.upload
  done

fi


echo -e "\n\nBuilding and Uploading DONE\n"
