#!/bin/sh

help() {
    echo "use: $0 production <tag>"
    echo "use: $0 latest"
    echo "Environment must provide:"
    echo "  TRAVIS_BUILD_DIR DEPLOY_USER DEPLOY_HOST DEPLOY_PATH"
    exit 1
}

if [ -z "$TRAVIS_BUILD_DIR" -o -z "$DEPLOY_USER" -o -z "$DEPLOY_HOST" -o -z "$DEPLOY_PATH" ]; then
    echo "missing DEPLOY_{USER,HOST,PATH}"
    exit 1
elif [ $# -eq 0 ]; then
    help
elif [ $1 = production ]; then
    LOCATION="$2"
elif [ $1 = latest ]; then
    LOCATION="LATEST"
else
    help
fi

rsync -rt --delete-after --quiet \
    -e 'ssh -o "stricthostkeychecking=no"' \
    $TRAVIS_BUILD_DIR/site/target/repository/ \
    ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_PATH}/$LOCATION
