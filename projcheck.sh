#!/bin/bash
JAVAFX_PATH="C:\\\\Program Files\\\\Java\\\\javafx-sdk-19"
JAVAFX_GLOBAL_LIB_NAME="lib"
JAVA_VERSION=17
# To maintain this script:
# Set up HERE to point to the script location. pushd ~/bin, then pushd back and:
# pushd;cp $HERE/tools/projcheck23q2.sh projcheck;pushd;pwd;projcheck

if ls *.iml >/dev/null 2>&1; then
    file="*.iml"
elif ls .idea/*.iml >/dev/null 2>&1; then
    file=".idea/*.iml"
else
    file=""
    echo "Not so good: Can't find a project .iml file"
fi

if [ ! -z "$file" ] ; then
    echo "Project .iml file:" $file
    
    if grep 'level="application"' $file >/dev/null 2>&1; then
        echo "Good: The project uses a global library."
    else
        echo "Not so good. The project does not appear to use the global JavaFX library. See $file"
    fi

    if grep 'library.*name="'$JAVAFX_GLOBAL_LIB_NAME'"' $file >/dev/null 2>&1; then
        echo "Good: The project uses the correct name for the global library."
    else
        echo "Not so good. The project does not appear to use the name \"$JAVAFX_GLOBAL_LIB_NAME\" for the global JavaFX library. See $file"
    fi

fi

if grep "out/" .gitignore>/dev/null 2>&1; then
    echo "Good: The project ignores build files."
else
    echo "Not so good: The project does not appear to ignore build artifacts. See $file"
fi

file=.gitignore
if grep ".idea/workspace.xml" $file >/dev/null 2>&1; then
    echo "Good: The project ignores workspace.xml"
else
    echo "Not so good: The project does not appear to ignore workspace.xml. See $file"
fi


file=.idea/vcs.xml
if ! grep "\.\." $file >/dev/null 2>&1; then
    echo "Good: The project does not reference outer projects."
else
    echo "Not so good: The project has a link to an outer project's Git. See $file"
fi


file=.idea/misc.xml
if grep "jdk-name=.$JAVA_VERSION"'"' $file >/dev/null 2>&1; then
    echo "Good: The project is configured to use the right version of java."
else
    echo "Not so good: The does not appear to be configured to use the right version of Java. See $file"
fi

file=".idea/runConfigurations/*.xml"
if ! ls $file >/dev/null 2>&1; then
    file=""
    echo "Not so good: The project does not appear to save the runtime configurations to the the project.  In the Edit Run Configurations dialog, check the box titled 'Store as project file'"
fi

if [ ! -z "$file" ] ; then
    if grep "$JAVAFX_PATH" $file >/dev/null 2>&1; then
        echo "Good: The project is configured to use the right version of javaFX."
    else
        echo "Not so good: The does not appear to be configured to use the right version of JavaFX in the runtime configurations. See $file"
    fi

    if grep 'VM_PARAMETERS.*'"$JAVAFX_PATH" $file >/dev/null 2>&1; then
        echo "Good: The project specifies JAVAFX as a VM option."
    else
        echo "Not so good: The project does not seem to specify VM Options with the right JavaFX version in the runtime configurations. This can happen if you accidentally paste the VM options into the wrong field, or if you have the wrong JavaFX version in the runtime options. See $file"
    fi
fi
