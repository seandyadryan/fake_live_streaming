"""Verify 64-bit native ELF LOAD alignment in the final signed APK."""
from pathlib import Path
from zipfile import ZipFile
import struct
import json

ROOT = Path(__file__).resolve().parents[1]
apk = ROOT / "app/build/outputs/apk/release/app-release.apk"
results = []
with ZipFile(apk) as archive:
    for name in archive.namelist():
        if not name.endswith(".so") or not name.startswith(("lib/arm64-v8a/", "lib/x86_64/")):
            continue
        data = archive.read(name)
        assert data[:5] == b"\x7fELF\x02", f"Expected ELF64: {name}"
        order = "<" if data[5] == 1 else ">"
        phoff = struct.unpack_from(order + "Q", data, 32)[0]
        entsize, count = struct.unpack_from(order + "HH", data, 54)
        alignments = []
        for index in range(count):
            offset = phoff + index * entsize
            if struct.unpack_from(order + "I", data, offset)[0] == 1:
                alignment = struct.unpack_from(order + "Q", data, offset + 48)[0]
                assert alignment >= 16384, f"Insufficient LOAD alignment: {name}: {alignment}"
                alignments.append(alignment)
        results.append({"library": name, "load_alignment_bytes": alignments})
assert results, "No 64-bit native libraries found; inspect the APK before claiming an alignment check."
report = ROOT / "output/native-alignment.json"
report.write_text(json.dumps(results, indent=2) + "\n", encoding="utf-8")
print(f"Verified 16 KB or greater ELF LOAD alignment for {len(results)} native libraries.")
