#!/usr/bin/env python3
"""Generate Android launcher PNGs from the selected branding source."""

from collections import deque
from pathlib import Path

from PIL import Image


PROJECT_ROOT = Path(__file__).resolve().parents[1]
SOURCE = PROJECT_ROOT / "docs/branding/file-manager-plus-ultra-icon-source.png"
RESOURCE_ROOT = PROJECT_ROOT / "app/src/main/res"

DENSITIES = {
    "mdpi": 1.0,
    "hdpi": 1.5,
    "xhdpi": 2.0,
    "xxhdpi": 3.0,
    "xxxhdpi": 4.0,
}

# Keep the subject comfortably inside Android's adaptive-icon safe zone. Samsung launchers apply
# an additional mask/zoom, so a full-canvas foreground crops most of the folder at the bottom.
FOREGROUND_CONTENT_SCALE = 0.64


def remove_connected_black_border(image: Image.Image) -> Image.Image:
    """Make only the near-black region connected to the canvas edge transparent."""
    image = image.convert("RGBA")
    width, height = image.size
    pixels = image.load()
    visited = bytearray(width * height)
    queue: deque[tuple[int, int]] = deque()

    def is_border_black(x: int, y: int) -> bool:
        red, green, blue, _ = pixels[x, y]
        return max(red, green, blue) <= 12 and max(red, green, blue) - min(red, green, blue) <= 6

    def enqueue(x: int, y: int) -> None:
        index = y * width + x
        if not visited[index] and is_border_black(x, y):
            visited[index] = 1
            queue.append((x, y))

    for x in range(width):
        enqueue(x, 0)
        enqueue(x, height - 1)
    for y in range(height):
        enqueue(0, y)
        enqueue(width - 1, y)

    while queue:
        x, y = queue.popleft()
        red, green, blue, _ = pixels[x, y]
        pixels[x, y] = red, green, blue, 0
        if x:
            enqueue(x - 1, y)
        if x + 1 < width:
            enqueue(x + 1, y)
        if y:
            enqueue(x, y - 1)
        if y + 1 < height:
            enqueue(x, y + 1)
    return image


def write_icon(image: Image.Image, size: int, destination: Path) -> None:
    destination.parent.mkdir(parents=True, exist_ok=True)
    resized = image.resize((size, size), Image.Resampling.LANCZOS)
    resized.save(destination, format="PNG", optimize=True)


def inset_foreground(image: Image.Image) -> Image.Image:
    """Center a scaled copy on the original transparent canvas for adaptive launchers."""
    image = image.convert("RGBA")
    content_size = round(image.width * FOREGROUND_CONTENT_SCALE)
    content = image.resize((content_size, content_size), Image.Resampling.LANCZOS)
    foreground = Image.new("RGBA", image.size, (0, 0, 0, 0))
    offset = (image.width - content_size) // 2
    foreground.alpha_composite(content, (offset, offset))
    return foreground


def main() -> None:
    source = remove_connected_black_border(Image.open(SOURCE))
    adaptive_foreground = inset_foreground(source)
    for density, scale in DENSITIES.items():
        directory = RESOURCE_ROOT / f"mipmap-{density}"
        write_icon(source, round(48 * scale), directory / "launcher_icon.png")
        write_icon(
            adaptive_foreground,
            round(108 * scale),
            directory / "launcher_icon_foreground.png",
        )


if __name__ == "__main__":
    main()
