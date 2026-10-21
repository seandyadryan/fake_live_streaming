"""Black-box startup and live-room check of the signed, minified production APK."""
from pathlib import Path
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

PACKAGE = "com.deploydulupulangnanti.fakelivestream"
OUT = Path("output/release-smoke")
OUT.mkdir(parents=True, exist_ok=True)

def adb(*args):
    return subprocess.check_output(["adb", *args], text=True)

def ui(name):
    adb("shell", "uiautomator", "dump", "/sdcard/livemuse-release.xml")
    xml = adb("exec-out", "cat", "/sdcard/livemuse-release.xml")
    (OUT / f"{name}.xml").write_text(xml, encoding="utf-8")
    return xml

try:
    print(adb("install", "-r", sys.argv[1]))
    adb("logcat", "-b", "crash", "-c")
    print(adb("shell", "am", "start", "-W", "-n", PACKAGE + "/.MainActivity"))
    studio = ui("studio")
    assert "LiveMuse" in studio, "Studio did not render in the production APK."
    adb("shell", "input", "swipe", "500", "1500", "500", "500", "350")
    options = ET.fromstring(ui("options"))
    button = next(node for node in options.iter("node") if node.get("text") == "Start practice live")
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", button.attrib["bounds"]))
    assert x2 > x1 and y2 > y1, "Start button is outside the viewport."
    adb("shell", "input", "tap", str((x1+x2)//2), str((y1+y2)//2))
    assert "SIMULATED" in ui("live"), "Live room did not render in the production APK."
    assert adb("shell", "pidof", PACKAGE).strip(), "Production process exited."
    crashes = adb("logcat", "-b", "crash", "-d")
    assert "FATAL EXCEPTION" not in crashes, crashes
    (OUT / "result.txt").write_text("PASS: Signed, minified APK installed; Studio and live room rendered; no fatal crash.\n", encoding="utf-8")
    print("PASS: Production APK startup and live room.")
finally:
    adb("shell", "screencap", "-p", "/sdcard/livemuse-release.png")
    adb("pull", "/sdcard/livemuse-release.png", str(OUT / "release.png"))
    (OUT / "crash-log.txt").write_text(adb("logcat", "-b", "crash", "-d"), encoding="utf-8")
