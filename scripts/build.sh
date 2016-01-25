#!/bin/bash

# Program arguments
#
# -b | --build:			Builds a new clean copy of the environment into the current path
# -u | --update:		Updates the current copy of the environment (current path)
# -h | --help:			Shows the script help
#


GITHUB_WIREZ=https://github.com/romartin/wirez.git
GITHUB_WIREZ_BRANCH=wirez
GITHUB_LIENZO_CORE=https://github.com/romartin/lienzo-core.git
GITHUB_LIENZO_CORE_BRANCH=wirez
GITHUB_JBPM_DESIGNER=https://github.com/romartin/jbpm-designer.git
GITHUB_JBPM_DESIGNER_BRANCH=master

LIENZO_CORE_PATH=lienzo-core-wirez
JBPM_DESIGNER_PATH=jbpm-designer-wirez
WIREZ_PATH=wirez

function cloneAndbuildModule() {
	
	git clone $1 $3
	cd $3
	git checkout -b $2 origin/$2
	mvn clean install -DskipTests
	cd ..

}

function updateModule() {

	cd $1
	git pull
	mvn clean install -DskipTests
	cd ..

}

function build() {

	echo "Building a new clean copy of Wirez environment..."

	if [ -d "$LIENZO_CORE_PATH" ]; then
		echo "[ERROR] - Lienzo core path already exists."
		exit 1
	fi

	if [ -d "$JBPM_DESIGNER_PATH" ]; then
		echo "[ERROR] - Jbpm designer path already exists."
		exit 1
	fi

	if [ -d "$WIREZ_PATH" ]; then
		echo "[ERROR] - Wirez path already exists."
		exit 1
	fi

	cloneAndbuildModule $GITHUB_LIENZO_CORE $GITHUB_LIENZO_CORE_BRANCH $LIENZO_CORE_PATH

	cloneAndbuildModule $GITHUB_JBPM_DESIGNER $GITHUB_JBPM_DESIGNER_BRANCH $JBPM_DESIGNER_PATH

	cloneAndbuildModule $GITHUB_WIREZ $GITHUB_WIREZ_BRANCH $WIREZ_PATH
	
	echo "Build finished"
}

function update() {

	echo "Updating current copy of Wirez environment..."

	if [ ! -d "$LIENZO_CORE_PATH" ]; then
		echo "[ERROR] - Lienzo core path does not exist."
		exit 1
	fi

	if [ ! -d "$JBPM_DESIGNER_PATH" ]; then
		echo "[ERROR] - Jbpm designer path does not exist."
		exit 1
	fi

	if [ ! -d "$WIREZ_PATH" ]; then
		echo "[ERROR] - Wirez path does not exist."
		exit 1
	fi

	updateModule $LIENZO_CORE_PATH

	updateModule $JBPM_DESIGNER_PATH

	updateModule $WIREZ_PATH
	
	echo "Update finished"
}

function usage
{
    echo "usage: wirez.sh [ [-b] | [-h]]"
}

set -e
while [ "$1" != "" ]; do
    case $1 in
        -b | --build ) shift
                                build
                                ;;
        -u | --update ) shift
                                update
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done

exit 0
