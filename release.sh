#!/usr/bin/env bash

set -e

if [[ `git status --porcelain` ]]; then
    echo "There are local changes, aborting."
    exit 1
fi

if [[ `git symbolic-ref --short -q HEAD` != "master" ]]; then
    echo "Must be on master branch, aborting."
    exit 1
fi

git fetch origin master -q
if [[ "$(git rev-parse HEAD)" != "$(git rev-parse master@{upstream})" ]]; then
    echo "Not up to date with origin, aborting."
    exit 1
fi

echo "Current version: `cat version`"
read -p "Next version: " next_version

git checkout -b release-$next_version
echo "$next_version" > version

./gradlew clean build

read -p "Do you want to release version '$next_version'? " yn
case $yn in
    [Yy]* ) ;;
    * ) exit 1;;
esac

git add version && git commit -m "Release $next_version :rocket:"
git push origin release-$next_version

git tag $next_version
git push origin $next_version

./gradlew build publish closeAndReleaseRepository -Prelease
