#!/usr/bin/env bash
set -euo pipefail
package=com.deploydulupulangnanti.fakelivestream
mkdir -p output/device-tests
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
trap 'adb shell wm size reset; adb shell wm density reset' EXIT
for spec in phone:1080x1920:420 tablet-7:1440x2560:280 tablet-10:1440x2560:200 desktop:1920x1080:160; do
  IFS=: read -r name size density <<< "$spec"
  adb shell wm size "$size"
  adb shell wm density "$density"
  adb shell am force-stop "$package"
  adb shell am instrument -w -e capture true "$package.test/androidx.test.runner.AndroidJUnitRunner" | tee "output/device-tests/$name.txt"
  grep -q 'OK (1 test)' "output/device-tests/$name.txt"
  mkdir -p "store/screenshots/$name"
  adb pull "/sdcard/Android/data/$package/files/screenshots/." "store/screenshots/$name/"
done
