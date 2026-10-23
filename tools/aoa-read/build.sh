#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK="${ANDROID_HOME:-/home/sf/.local/opt/android-sdk}"
export JAVA_HOME="${JAVA_HOME:-/home/sf/.local/opt/jdk-17}"
export PATH="$JAVA_HOME/bin:$PATH"
JAR="$SDK/platforms/android-34/android.jar"
BT="$SDK/build-tools/35.0.0"
KS="${HOME}/.android/debug.keystore"
cd "$ROOT"
rm -rf build
mkdir -p build/{gen,classes,dex}
"$BT/aapt" package -f -m -J build/gen -M AndroidManifest.xml -S res -I "$JAR"
find src build/gen -name '*.java' > build/src.list
javac --release 17 -cp "$JAR" -d build/classes @"build/src.list"
"$BT/d8" --min-api 26 --lib "$JAR" --output build/dex $(find build/classes -name '*.class')
"$BT/aapt" package -f -M AndroidManifest.xml -S res -I "$JAR" -F build/unsigned.apk
"$BT/aapt" add build/unsigned.apk build/dex/classes.dex
# aapt add stores the path; flatten
python3 - <<'PY'
import zipfile, os, shutil
src = "build/unsigned.apk"
tmp = "build/unsigned2.apk"
with zipfile.ZipFile(src) as zin, zipfile.ZipFile(tmp, "w") as zout:
    for i in zin.infolist():
        data = zin.read(i.filename)
        name = "classes.dex" if i.filename.endswith("classes.dex") else i.filename
        zout.writestr(name, data)
os.replace(tmp, src)
PY
"$BT/zipalign" -f 4 build/unsigned.apk build/aligned.apk
"$BT/apksigner" sign --ks "$KS" --ks-pass pass:android --key-pass pass:android \
  --out build/aoa-read.apk build/aligned.apk
"$BT/apksigner" verify build/aoa-read.apk
ls -l build/aoa-read.apk
