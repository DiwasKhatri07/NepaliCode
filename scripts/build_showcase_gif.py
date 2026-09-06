from pathlib import Path
from PIL import Image, ImageOps

ROOT = Path(__file__).resolve().parents[1]
source_dir = ROOT / "docs" / "assets" / "screenshots"
output = ROOT / "docs" / "assets" / "nepalicode-mobile-showcase.gif"

paths = sorted(source_dir.glob("0[1-5]-*.jpg"))
frames = []
for path in paths:
    with Image.open(path) as image:
        image = ImageOps.exif_transpose(image).convert("RGB")
        image.thumbnail((360, 800), Image.Resampling.LANCZOS)
        canvas = Image.new("RGB", (360, 800), (14, 16, 24))
        x = (canvas.width - image.width) // 2
        y = (canvas.height - image.height) // 2
        canvas.paste(image, (x, y))
        frames.append(canvas.convert("P", palette=Image.Palette.ADAPTIVE, colors=128))

if not frames:
    raise SystemExit("No screenshot frames found")

frames[0].save(
    output,
    save_all=True,
    append_images=frames[1:],
    duration=[1200, 1200, 1200, 1400, 1700],
    loop=0,
    optimize=True,
)
print(f"Wrote {output} with {len(frames)} frames")
