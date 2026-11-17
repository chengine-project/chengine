#!/usr/bin/env python3
"""Synchronize and verify the complete examples embedded in the docs."""
from pathlib import Path
import argparse
import re

ROOT = Path(__file__).resolve().parent.parent
EXAMPLES = {
    "catalog/CatalogHandler.kt": "CatalogHandler.kt",
    "conversation/ConversationHandler.kt": "ConversationHandler.kt",
    "inline-keyboard/InlineKeyboardHandler.kt": "InlineKeyboardHandler.kt",
    "edit-message/EditMessageHandler.kt": "EditMessageHandler.kt",
    "media/MediaHandler.kt": "MediaHandler.kt",
    "polls/PollHandler.kt": "PollHandler.kt",
}
LOCALES = ("en", "zh", "es", "de")


def structure(text):
    # Ignore human-facing strings and comments while checking the Kotlin structure.
    text = re.sub(r'"(?:\\.|[^"\\])*"', '""', text)
    text = re.sub(r'/\*.*?\*/|//[^\n]*', '', text, flags=re.S)
    return re.sub(r'\s+', '', text)


def routing_strings(text):
    patterns = (
        r'@HandleCommand\("([^"]+)"\)',
        r'callbackData\s*=\s*"([^"]+)"',
        r'session\["([^"]+)"\]',
        r'GraphTransition\.(?:Await|Next)\("([^"]+)"',
        r'"((?:catalog:|edit_|check_button)[^"]*)"',
    )
    return [re.findall(pattern, text) for pattern in patterns]


parser = argparse.ArgumentParser()
parser.add_argument("--check", action="store_true")
args = parser.parse_args()
outdated = []
for src, dest in EXAMPLES.items():
    source = ROOT / "examples" / src
    target = ROOT / "docs/_includes/examples" / dest
    contents = source.read_bytes()
    if not target.exists() or target.read_bytes() != contents:
        if args.check:
            outdated.append(str(target.relative_to(ROOT)))
        else:
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(contents)
    if args.check:
        source_text = contents.decode("utf-8")
        for locale in LOCALES:
            translated = ROOT / "docs/_includes/examples" / locale / dest
            if not translated.exists():
                outdated.append(str(translated.relative_to(ROOT)))
                continue
            translated_text = translated.read_text(encoding="utf-8")
            if structure(source_text) != structure(translated_text) or routing_strings(source_text) != routing_strings(translated_text):
                outdated.append(str(translated.relative_to(ROOT)))
if outdated:
    raise SystemExit("Documentation examples need updating: " + ", ".join(outdated) + ". Run python3 scripts/sync-doc-examples.py and update translations.")
print("Documentation examples are up to date" if args.check else "Source examples synchronized; review localized copies")
