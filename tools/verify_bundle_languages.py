"""Verify the delivered AAB keeps language resources installed together.

Field numbers follow Google's bundletool schema:
https://github.com/google/bundletool/blob/master/src/main/proto/config.proto
"""
from pathlib import Path
from zipfile import ZipFile
import sys


def _varint(data, position):
    value = shift = 0
    while position < len(data) and shift < 70:
        byte = data[position]
        position += 1
        value |= (byte & 127) << shift
        if byte < 128:
            return value, position
        shift += 7
    raise ValueError("Invalid protobuf varint")


def _fields(data):
    result = {}
    position = 0
    while position < len(data):
        key, position = _varint(data, position)
        number, wire = key >> 3, key & 7
        if wire == 0:
            value, position = _varint(data, position)
        elif wire in (1, 2, 5):
            if wire == 2:
                size, position = _varint(data, position)
            else:
                size = 8 if wire == 1 else 4
            end = position + size
            if end > len(data):
                raise ValueError("Truncated protobuf field")
            value, position = data[position:end], end
        else:
            raise ValueError(f"Unsupported protobuf wire type: {wire}")
        result.setdefault(number, []).append(value)
    return result


def assert_languages_installed(bundle):
    with ZipFile(bundle) as archive:
        config = _fields(archive.read("BundleConfig.pb"))
    # BundleConfig.optimizations -> Optimizations.splits_config -> split_dimension.
    optimizations = _fields(config[2][0])
    splits = _fields(optimizations[1][0])
    dimensions = [_fields(value) for value in splits.get(1, [])]
    language = [dimension for dimension in dimensions if dimension.get(1) == [3]]
    if len(language) != 1 or language[0].get(2) != [1]:
        raise ValueError("Language splitting is enabled; offline chat languages may be missing after a phone-language change.")


if __name__ == "__main__":
    root = Path(__file__).resolve().parents[1]
    bundle = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "app/build/outputs/bundle/release/app-release.aab"
    assert_languages_installed(bundle)
    print("Verified AAB language splitting is disabled (LANGUAGE negate=true).")
