#!/usr/bin/env bash

if which wget >/dev/null; then
    echo "wget exists"
else
    echo "wget not installed, exiting"
    exit
fi

if which mvn >/dev/null; then
    echo "mvn exists"
else
    echo "maven not installed, exiting"
    exit
fi


mkdir tmp
if [ ! -d data ]; then
	mkdir data
fi

cd tmp
# Download data
if [ ! -d ../data/ACE2004 ]; then
	wget --no-check-certificate https://cogcomp.cs.illinois.edu/member_pages/sammons/tmp/ACE-2004.zip .
	unzip ACE-2004.zip
        rm -rf ace_tides_multling_train/data/Chinese
        rm -rf ace_tides_multling_train/data/Arabic
        mv ace_tides_multling_train/data/English/*.dtd ace_tides_multling_train/data/.
        mv ace_tides_multling_train/data/English/FileList_English ace_tides_multling_train/data/.
        cp -r ace_tides_multling_train/dtd/* ace_tides_multling_train/data/English/arabic_treebank/.
        cp -r ace_tides_multling_train/dtd/* ace_tides_multling_train/data/English/nwire/.
        cp -r ace_tides_multling_train/dtd/* ace_tides_multling_train/data/English/fisher_transcripts/.
        cp -r ace_tides_multling_train/dtd/* ace_tides_multling_train/data/English/chinese_treebank/.
        cp -r ace_tides_multling_train/dtd/* ace_tides_multling_train/data/English/bnews/.
	mv ace_tides_multling_train ../data/ACE2004
fi

if [ ! -d ../data/ACE2005 ]; then
	wget --no-check-certificate https://cogcomp.cs.illinois.edu/member_pages/sammons/tmp/ACE-2005-English.zip . 
	unzip ACE-2005-English.zip
	mv ACE05_English ../data/ACE2005
	### The below cp command is to ensure that the reader can read bn docs correctly.
	mv ../data/ACE2005/bc/timex2norm/* ../data/ACE2005/bc/.
	mv ../data/ACE2005/bn/timex2norm/apf.v5.1.1.dtd ../data/ACE2005/bn/.
	mv ../data/ACE2005/cts/timex2norm/* ../data/ACE2005/cts/.
	mv ../data/ACE2005/nw/timex2norm/* ../data/ACE2005/nw/.
	mv ../data/ACE2005/un/timex2norm/* ../data/ACE2005/un/.
	mv ../data/ACE2005/wl/timex2norm/* ../data/ACE2005/wl/.
fi

# Download reader and do MVN install.
# Note the one on remote Cogcomp maven is not up to date, and can't be used in our project. 
# We have to manually download and install them.

# Once you have illinois-ace-reader installed, you may need to restart your IDE, 
# or re-import mvn project to refresh its cache to use the new version.

#if [ ! -d illinois-ace-reader ]; then
#	wget --no-check-certificate https://cogcomp.cs.illinois.edu/member_pages/sammons/tmp/illinois-ace-reader.zip .
#	unzip illinois-ace-reader.zip
#	cd illinois-ace-reader
## Install ace reader.
#	mvn install -DskipTests
#        cd ..
#fi

cd ..
rm -r tmp
