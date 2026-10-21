"""Bundle only the signed binaries and public store materials for handoff."""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
import hashlib
import json

ROOT = Path(__file__).resolve().parents[1]
DEST = ROOT / "output/release"
DEST.mkdir(parents=True, exist_ok=True)
binaries = [ROOT / "app/build/outputs/bundle/release/app-release.aab",
            ROOT / "app/build/outputs/apk/release/app-release.apk"]
mapping = ROOT / "app/build/outputs/mapping/release/mapping.txt"
if mapping.is_file():
    binaries.append(mapping)
for path in binaries:
    if not path.is_file():
        raise SystemExit(f"Missing release artifact: {path}")
materials = sorted(p for p in (ROOT / "store").rglob("*") if p.is_file())
screenshots = list((ROOT / "store/screenshots").rglob("*.png"))
assert len(screenshots) >= 20, "Capture five screenshots in each device group before packaging."
assert (ROOT / "store/promo/livemuse-promo.mp4").is_file(), "Create the promo first."
hashes = {path.name: hashlib.sha256(path.read_bytes()).hexdigest() for path in binaries}
(DEST / "SHA256SUMS.json").write_text(json.dumps(hashes, indent=2) + "\n", encoding="utf-8")
archive = DEST / "LiveMuse-1.0.0-PlayStore.zip"
with ZipFile(archive, "w", ZIP_DEFLATED, compresslevel=5) as bundle:
    for path in binaries:
        bundle.write(path, "release/" + path.name)
    for path in materials + [ROOT / "docs/index.html", ROOT / "README.md"]:
        bundle.write(path, str(path.relative_to(ROOT)))
    bundle.write(DEST / "SHA256SUMS.json", "release/SHA256SUMS.json")
print(f"Created {archive} ({archive.stat().st_size:,} bytes)")
