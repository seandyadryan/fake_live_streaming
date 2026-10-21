"""Create a silent 1080p promo from actual app screenshots, without mocked UI."""
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "output/python-deps"))
import imageio_ffmpeg
from PIL import Image, ImageDraw
from create_store_assets import font, INK, VIOLET, MINT

WORK = ROOT / "output" / "promo-frames"
DEST = ROOT / "store" / "promo"
WORK.mkdir(parents=True, exist_ok=True)
DEST.mkdir(parents=True, exist_ok=True)
slides = [
    ("01-studio.png", "Your stage.\nYour moment.", "A private space to practice your live vibe."),
    ("02-live.png", "Find your flow.", "A virtual crowd. Friendly chat. Zero pressure."),
    ("03-gifts.png", "A little extra\nmagic.", "Surprise gifts with no real-money value."),
    ("04-summary.png", "Look at you go.", "Save your progress. Build your confidence.")
]
for index, (file, headline, subtitle) in enumerate(slides):
    image = Image.new("RGB", (1920, 1080), INK)
    draw = ImageDraw.Draw(image)
    draw.text((100, 95), "LiveMuse", font=font(38, True), fill=VIOLET)
    draw.multiline_text((100, 310), headline, font=font(85, True), fill="white", spacing=4)
    draw.text((105, 590), subtitle, font=font(26), fill=(180, 183, 204))
    draw.text((105, 820), "LIVE STREAM SIMULATOR", font=font(20, True), fill=MINT)
    draw.text((105, 865), "Simulated audience. No real broadcast.", font=font(22), fill=(180, 183, 204))
    shot = Image.open(ROOT / "store/screenshots/phone" / file).convert("RGB")
    shot.thumbnail((540, 960), Image.Resampling.LANCZOS)
    image.paste(shot, (1260, 60))
    image.save(WORK / f"slide-{index}.png")

ffmpeg = imageio_ffmpeg.get_ffmpeg_exe()
segments = []
for index in range(len(slides)):
    output = WORK / f"segment-{index}.mp4"
    subprocess.run([ffmpeg, "-y", "-loop", "1", "-i", str(WORK / f"slide-{index}.png"),
                    "-t", "5", "-r", "30", "-c:v", "libx264", "-threads", "2", "-preset", "medium",
                    "-pix_fmt", "yuv420p", "-vf", "fade=t=in:st=0:d=0.3,fade=t=out:st=4.7:d=0.3",
                    "-an", str(output)], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE)
    segments.append(output)
concat = WORK / "concat.txt"
concat.write_text("\n".join(f"file '{str(p).replace(chr(92), '/')}'" for p in segments), encoding="utf-8")
subprocess.run([ffmpeg, "-y", "-f", "concat", "-safe", "0", "-i", str(concat), "-c", "copy",
                "-movflags", "+faststart", str(DEST / "livemuse-promo.mp4")],
               check=True, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE)
print(f"Created {DEST / 'livemuse-promo.mp4'} (1920x1080, 20 seconds, silent).")
