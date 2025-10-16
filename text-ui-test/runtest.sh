#!/usr/bin/env bash

# change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Normalize ACTUAL.TXT:
# 1. Replace 8-char lowercase hex IDs with # prefix: #abcd1234 -> #XXXXXXXX
sed -E 's/#[a-f0-9]{8}/#XXXXXXXX/g' ACTUAL.TXT > ACTUAL-NORMALIZED.TXT

# 2. Replace standalone 8-char lowercase hex IDs: abcd1234 -> XXXXXXXX
sed -E 's/\b[a-f0-9]{8}\b/XXXXXXXX/g' ACTUAL-NORMALIZED.TXT > ACTUAL-NORMALIZED2.TXT

# 3. Normalize dates: YYYY-MM-DD -> XXXX-XX-XX
sed -E 's/[0-9]{4}-[0-9]{2}-[0-9]{2}/XXXX-XX-XX/g' ACTUAL-NORMALIZED2.TXT > ACTUAL-NORMALIZED.TXT

# Copy and convert EXPECTED.TXT line endings
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL-NORMALIZED.TXT 2>/dev/null || true

# Normalize EXPECTED.TXT:
# 1. Replace 8-char lowercase hex IDs with # prefix
sed -E 's/#[a-f0-9]{8}/#XXXXXXXX/g' EXPECTED-UNIX.TXT > EXPECTED-NORMALIZED.TXT

# 2. Replace standalone 8-char lowercase hex IDs
sed -E 's/\b[a-f0-9]{8}\b/XXXXXXXX/g' EXPECTED-NORMALIZED.TXT > EXPECTED-NORMALIZED2.TXT

# 3. Normalize dates in expected file
sed -E 's/[0-9]{4}-[0-9]{2}-[0-9]{2}/XXXX-XX-XX/g' EXPECTED-NORMALIZED2.TXT > EXPECTED-NORMALIZED.TXT

# Compare the normalized files
diff EXPECTED-NORMALIZED.TXT ACTUAL-NORMALIZED.TXT
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi