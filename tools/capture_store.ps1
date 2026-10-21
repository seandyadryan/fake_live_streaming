param(
    [string]$SdkPath = "$env:LOCALAPPDATA\Android\Sdk",
    [string]$Serial = 'emulator-5554'
)
$ErrorActionPreference = 'Stop'
$adbPath = Join-Path $SdkPath 'platform-tools\adb.exe'
$repoRoot = Split-Path $PSScriptRoot -Parent
$package = 'com.deploydulupulangnanti.fakelivestream'
function Adb {
    & $adbPath -s $Serial @args
    if ($LASTEXITCODE -ne 0) { throw "ADB failed: $args" }
}
$originalSize = (Adb shell wm size) -join "`n"
$originalDensity = (Adb shell wm density) -join "`n"
try {
    Adb shell settings put global window_animation_scale 0
    Adb shell settings put global transition_animation_scale 0
    Adb shell settings put global animator_duration_scale 0
    Adb shell settings put secure show_ime_with_hard_keyboard 1
    $viewports = @(
        @{ name='phone'; size='1080x1920'; density='420' },
        @{ name='tablet-7'; size='1440x2560'; density='280' },
        @{ name='tablet-10'; size='1440x2560'; density='200' },
        @{ name='desktop'; size='1920x1080'; density='160' }
    )
    foreach ($viewport in $viewports) {
        Write-Output "Capturing $($viewport.name)"
        Adb shell wm size $viewport.size
        Adb shell wm density $viewport.density
        Adb shell am force-stop $package
        $result = Adb shell am instrument -w -e capture true "$package.test/androidx.test.runner.AndroidJUnitRunner"
        $result
        if (($result -join "`n") -notmatch 'OK \(2 tests\)') { throw "Instrumented test failed for $($viewport.name)" }
        $destination = Join-Path $repoRoot "store\screenshots\$($viewport.name)"
        New-Item -ItemType Directory -Force $destination | Out-Null
        Adb pull "/sdcard/Android/data/$package/files/screenshots/." $destination
    }
} finally {
    if ($originalSize -match 'Override size: (\d+x\d+)') { Adb shell wm size $Matches[1] } else { Adb shell wm size reset }
    if ($originalDensity -match 'Override density: (\d+)') { Adb shell wm density $Matches[1] } else { Adb shell wm density reset }
}
