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


def main() -> None:
    source = remove_connected_black_border(Image.open(SOURCE))
    for density, scale in DENSITIES.items():
        directory = RESOURCE_ROOT / f"mipmap-{density}"
        write_icon(source, round(48 * scale), directory / "launcher_icon.png")
        write_icon(source, round(108 * scale), directory / "launcher_icon_foreground.png")


if __name__ == "__main__":
    main()
