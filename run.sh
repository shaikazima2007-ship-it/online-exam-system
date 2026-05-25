#!/bin/bash

JAVAFX="/Users/shaikazima/Desktop/RTRP/javafx-sdk-25.0.2/lib"

MYSQL="/Users/shaikazima/Desktop/RTRP/IdeaProjects/OnlineExam/bin/main/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar"

echo "Compiling..."

javac \
--module-path "$JAVAFX" \
--add-modules javafx.controls,javafx.fxml \
-cp ".:$MYSQL" \
*.java

if [ $? -eq 0 ]; then
    echo "Running..."

    java \
    --module-path "$JAVAFX" \
    --add-modules javafx.controls,javafx.fxml \
    -cp ".:$MYSQL" \
    StartPage

else
    echo "Compilation failed"
fi