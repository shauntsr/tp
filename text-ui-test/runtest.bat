#!/usr/bin/env bash

# change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Normalize IDs - replace all 8-character hex IDs with a placeholder
sed -E 's/#[a-f0-9]{8}/#XXXXXXXX/g' ACTUAL.TXT > ACTUAL-NORMALIZED.TXT
sed -E 's/[a-f0-9]{8}( |$)/XXXXXXXX\1/g' ACTUAL-NORMALIZED.TXT > ACTUAL-NORMALIZED2.TXT
# Normalize dates - replace all dates with XXXX-XX-XX
sed -E 's/[0-9]{4}-[0-9]{2}-[0-9]{2}/XXXX-XX-XX/g' ACTUAL-NORMALIZED2.TXT > ACTUAL-NORMALIZED.TXT

cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL-NORMALIZED.TXT

# Normalize the expected file too
sed -E 's/#[a-f0-9]{8}/#XXXXXXXX/g' EXPECTED-UNIX.TXT > EXPECTED-NORMALIZED.TXT
sed -E 's/[a-f0-9]{8}( |$)/XXXXXXXX\1/g' EXPECTED-NORMALIZED.TXT > EXPECTED-NORMALIZED2.TXT
# Normalize dates in expected file
sed -E 's/[0-9]{4}-[0-9]{2}-[0-9]{2}/XXXX-XX-XX/g' EXPECTED-NORMALIZED2.TXT > EXPECTED-NORMALIZED.TXT

diff EXPECTED-NORMALIZED.TXT ACTUAL-NORMALIZED.TXT
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
