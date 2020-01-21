find -name "*.java" > sources.txt
javac -d bin @sources.txt $*
echo Finished.

