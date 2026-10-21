"""Render the established vector brand into Play Store artwork and validate exports."""
from pathlib import Path
import json
import math
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
STORE = ROOT / "store"
INK = (12, 13, 22)
VIOLET = (169, 152, 255)
MINT = (105, 231, 210)
CORAL = (255, 118, 150)
FONT_ROOT = Path("C:/Windows/Fonts")


def font(size, bold=False):
    candidates = [FONT_ROOT / ("segoeuib.ttf" if bold else "segoeui.ttf"),
                  Path("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf")]
    return ImageFont.truetype(str(next(p for p in candidates if p.exists())), size)


def mark(draw, x, y, scale):
    def points(coords):
        return [(x + a * scale, y + b * scale) for a, b in coords]
    draw.polygon(points([(31, 30), (43, 30), (43, 65), (69, 65), (69, 77), (31, 77)]), fill=VIOLET)
    draw.polygon(points([(58, 32), (80, 46), (58, 60)]), fill=CORAL)
    draw.ellipse((x + 73 * scale, y + 21 * scale, x + 83 * scale, y + 31 * scale), fill=MINT)


def brand_assets():
    (STORE / "graphics").mkdir(parents=True, exist_ok=True)
    icon = Image.new("RGB", (1024, 1024), (22, 19, 41))
    draw = ImageDraw.Draw(icon)
    mark(draw, -35, -15, 10.1)
    icon.resize((512, 512), Image.Resampling.LANCZOS).save(STORE / "graphics/app-icon-512.png")

    # Vector-based editorial graphic; no invented in-app interface.
    scale = 2
    canvas = Image.new("RGB", (1024 * scale, 500 * scale), INK)
    draw = ImageDraw.Draw(canvas)
    for y in range(1000):
        c = int(8 * (y / 1000))
        draw.line((0, y, 2048, y), fill=(12 + c, 13 + c // 2, 22 + c * 2))
    for radius in (155, 208, 261, 314):
        draw.ellipse(((820-radius)*2, (244-radius)*2, (820+radius)*2, (244+radius)*2), outline=(48, 39, 77), width=2)
    draw.rounded_rectangle((700*2, 130*2, 920*2, 350*2), radius=54*2, fill=(28, 24, 47), outline=(67, 54, 102), width=2)
    mark(draw, 704*2, 133*2, 2*2)
    draw.ellipse((922*2, 110*2, 944*2, 132*2), fill=MINT)
    draw.ellipse((680*2, 362*2, 694*2, 376*2), fill=CORAL)
    draw.text((60*2, 56*2), "LiveMuse", fill=(245, 243, 255), font=font(25*2, True))
    draw.text((60*2, 142*2), "Your stage.", fill=(245, 243, 255), font=font(57*2, True))
    draw.text((60*2, 209*2), "Your moment.", fill=VIOLET, font=font(57*2, True))
    draw.text((63*2, 300*2), "Practice your live vibe.", fill=(184, 186, 208), font=font(23*2))
    draw.rounded_rectangle((60*2, 386*2, 347*2, 427*2), radius=20*2, fill=(29, 42, 44))
    draw.text((79*2, 395*2), "LIVE STREAM SIMULATOR", fill=MINT, font=font(13*2, True))
    canvas.resize((1024, 500), Image.Resampling.LANCZOS).save(STORE / "graphics/feature-1024x500.png")


def validate():
    manifest = []
    for path in sorted(STORE.rglob("*.png")):
        with Image.open(path) as image:
            w, h = image.size
            size = path.stat().st_size
            if "screenshots" in path.parts:
                assert abs(w / h - 9 / 16) < .001 or abs(w / h - 16 / 9) < .001, (path, image.size)
                minimum = 1080 if ("desktop" in path.parts or "tablet-10" in path.parts) else 320
                maximum = 7680 if minimum == 1080 else 3840
                assert minimum <= min(w, h) and max(w, h) <= maximum
                assert size <= 8 * 1024 * 1024
            if path.name == "feature-1024x500.png":
                assert (w, h) == (1024, 500) and size <= 15 * 1024 * 1024
            if path.name == "app-icon-512.png":
                assert (w, h) == (512, 512)
            manifest.append({"file": str(path.relative_to(STORE)).replace("\\", "/"), "width": w, "height": h, "bytes": size, "mode": image.mode})
    short = (STORE / "short-description.txt").read_text(encoding="utf-8").strip()
    full = (STORE / "full-description.txt").read_text(encoding="utf-8").strip()
    assert len(short) <= 80
    assert len(full) <= 4000
    counts = {group: len(list((STORE / "screenshots" / group).glob("*.png")))
              for group in ("phone", "desktop", "tablet-7", "tablet-10")}
    if any(counts.values()):
        assert 2 <= counts["phone"] <= 8
        assert 4 <= counts["desktop"] <= 8
        assert 1 <= counts["tablet-7"] <= 8 and 1 <= counts["tablet-10"] <= 8
    report = {"short_description_characters": len(short), "full_description_characters": len(full), "screenshot_counts": counts, "assets": manifest}
    (STORE / "asset-manifest.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(f"Validated {len(manifest)} PNG assets. Description lengths: {len(short)} / {len(full)}.")


if __name__ == "__main__":
    brand_assets()
    validate()
