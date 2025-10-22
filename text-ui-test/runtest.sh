#!/usr/bin/env bash

# change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

# Remove previous test data directory so tests start clean
if [ -d data ]; then
  echo "Removing existing test data directory: ./data"
  rm -rf data
fi

# Run jar and feed input.txt line by line
JAR=$(find ../build/libs/ -mindepth 1 -print -quit)
(
while IFS= read -r line; do
    echo "$line"
    sleep 1   # slight delay for deterministic timestamps
done < input.txt
) | java -jar "$JAR" > ACTUAL.TXT

# Normalize ACTUAL.TXT:
# 1. Replace 8-char lowercase hex IDs with # prefix: #abcd1234 -> #XXXXXXXX
sed -E 's/#[0-9a-f]{8}/#XXXXXXXX/g' ACTUAL.TXT > ACTUAL-NORMALIZED.TXT

# 2. Replace standalone 8-char lowercase hex IDs at end of line or before space
sed -E 's/([^0-9a-f#]|^)([0-9a-f]{8})([^0-9a-f]|$)/\1XXXXXXXX\3/g' ACTUAL-NORMALIZED.TXT > ACTUAL-NORMALIZED2.TXT

# 3. Normalize dates: YYYY-MM-DD -> XXXX-XX-XX
sed -E 's/[0-9]{4}-[0-9]{2}-[0-9]{2}/XXXX-XX-XX/g' ACTUAL-NORMALIZED2.TXT > ACTUAL-NORMALIZED.TXT

# Copy and convert EXPECTED.TXT line endings
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL-NORMALIZED.TXT 2>/dev/null || true

# Normalize EXPECTED.TXT:
# 1. Replace 8-char lowercase hex IDs with # prefix
sed -E 's/#[0-9a-f]{8}/#XXXXXXXX/g' EXPECTED-UNIX.TXT > EXPECTED-NORMALIZED.TXT

# 2. Replace standalone 8-char lowercase hex IDs
sed -E 's/([^0-9a-f#]|^)([0-9a-f]{8})([^0-9a-f]|$)/\1XXXXXXXX\3/g' EXPECTED-NORMALIZED.TXT > EXPECTED-NORMALIZED2.TXT

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
