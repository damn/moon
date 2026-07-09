#!/usr/bin/env python3
"""Extract Java/LibGDX :import from clojure/* into mirrored com/java/org namespaces."""

import glob
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src")

SKIP_PATHS = {
    "src/clojure/scene2d/actor",
    "src/clojure/new_actor.clj",
    "src/clojure/srand.clj",
    "src/clojure/sshuffle.clj",
    "src/clojure/java/io/file.clj",
    "src/clojure/map_layers/add!.clj",
    "src/clojure/map_layers/get.clj",
    "src/clojure/pack!.clj",
    "src/clojure/set_fill_parent!.clj",
    "src/clojure/application.clj",
    "src/clojure/application_listener.clj",
    "src/clojure/files.clj",
    "src/clojure/file_handle.clj",
    "src/clojure/event.clj",
    "src/clojure/disposable.clj",
}


def camel_to_kebab(name: str) -> str:
    parts = name.split("$")
    kebabbed = []
    for part in parts:
        s = re.sub(r"([a-z0-9])([A-Z])", r"\1-\2", part)
        s = re.sub(r"([A-Z]+)([A-Z][a-z])", r"\1-\2", s)
        kebabbed.append(s.lower())
    return "$".join(kebabbed)


def kebab_to_file(name: str) -> str:
    return name.replace("-", "_")


def filename_key(path: str) -> str:
    base = os.path.basename(path).replace(".clj", "")
    return base.replace("_", "").replace("-", "").replace("$", "").lower()


def class_key(cls: str) -> str:
    return cls.replace("_", "").replace("$", "").lower()


def parse_imports(content: str) -> list[tuple[str, str]]:
    imports = []
    m = re.search(r"\(:import\b", content)
    if not m:
        return imports
    start = m.start()
    depth = 0
    block = ""
    for i in range(start, len(content)):
        c = content[i]
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
        block += c
        if depth == 0:
            break
    inner = block[len("(:import") :].strip()
    for gm in re.finditer(r"\(([\w.$]+)\s+([^)]+)\)", inner):
        pkg = gm.group(1)
        for cls in gm.group(2).split():
            imports.append((pkg, cls))
    return imports


def primary_class(imports: list[tuple[str, str]], body: str, path: str) -> tuple[str, str]:
    java_imports = [(p, c) for p, c in imports if p.startswith(("com.", "org.", "java."))]
    hint = filename_key(path)
    for pkg, cls in java_imports:
        if class_key(cls) == hint or class_key(cls).endswith(hint):
            return pkg, cls
    scores = {}
    for pkg, cls in java_imports:
        scores[(pkg, cls)] = len(re.findall(rf"\b{re.escape(cls)}\b", body))
    if scores:
        return max(scores.items(), key=lambda x: x[1])[0]
    return java_imports[0]


def mirror_ns(pkg: str, cls: str) -> str:
    return f"{pkg}.{camel_to_kebab(cls)}"


def mirror_path(ns: str) -> str:
    parts = ns.split(".")
    file_part = kebab_to_file(parts[-1])
    return os.path.join(SRC, *parts[:-1], file_part + ".clj")


def extract_ns(content: str) -> str | None:
    m = re.search(r"\(ns\s+([\w.$-]+)", content)
    return m.group(1) if m else None


def extract_parens_form(content: str, start: int) -> str:
    depth = 0
    form = ""
    for i in range(start, len(content)):
        c = content[i]
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
        form += c
        if depth == 0:
            return form
    return form


def extract_body(content: str) -> str:
    m = re.search(r"\(ns\s+", content)
    if not m:
        return ""
    ns_form = extract_parens_form(content, m.start())
    return content[m.start() + len(ns_form) :].lstrip("\n").strip()


def extract_refer_exclude(content: str) -> str | None:
    m = re.search(r"\(:refer-clojure\s+:exclude\s+\[[^\]]+\]\)", content)
    return m.group(0) if m else None


def build_mirror_content(mirror_ns_name: str, imports: list[tuple[str, str]], body: str, refer_exclude: str | None) -> str:
    by_pkg: dict[str, list[str]] = {}
    for pkg, cls in imports:
        if pkg.startswith(("com.", "org.", "java.")):
            by_pkg.setdefault(pkg, [])
            if cls not in by_pkg[pkg]:
                by_pkg[pkg].append(cls)
    lines = [f"(ns {mirror_ns_name}"]
    if refer_exclude:
        lines.append(f"  {refer_exclude}")
    lines.append("  (:import")
    for pkg in sorted(by_pkg):
        lines.append(f"           ({pkg} {' '.join(by_pkg[pkg])})")
    lines.append("           ))")
    lines.append("")
    lines.append(body)
    lines.append("")
    return "\n".join(lines)


def build_clojure_wrapper(clojure_ns: str, mirror_ns_name: str, body: str, refer_exclude: str | None) -> str:
    alias = mirror_ns_name.split(".")[-1].replace("$", "-")
    lines = [f"(ns {clojure_ns}"]
    if refer_exclude:
        lines.append(f"  {refer_exclude}")
    lines.append(f"  (:require [{mirror_ns_name} :as {alias}]))")
    lines.append("")

    pos = 0
    while pos < len(body):
        m = re.search(r"\((defn|def)\s", body[pos:])
        if not m:
            break
        start = pos + m.start()
        form = extract_parens_form(body, start)
        pos = start + len(form)
        name_m = re.match(r"\((defn|def)\s+([\w!?.$>+-]+)", form)
        if not name_m:
            continue
        kind, name = name_m.group(1), name_m.group(2)
        if kind == "defn":
            lines.append(f"(defn {name} [& args]")
            lines.append(f"  (apply {alias}/{name} args))")
        else:
            lines.append(f"(def {name}")
            lines.append(f"  {alias}/{name})")
        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


def should_skip(path: str) -> bool:
    for skip in SKIP_PATHS:
        if path.endswith(skip) or skip in path:
            return True
    return False


def process_file(path: str, dry_run: bool = False) -> bool:
    if should_skip(path):
        return False

    with open(path) as f:
        content = f.read()

    imports = parse_imports(content)
    java_imports = [(p, c) for p, c in imports if p.startswith(("com.", "org.", "java."))]
    if not java_imports:
        return False

    clojure_ns = extract_ns(content)
    if not clojure_ns:
        return False

    body = extract_body(content)
    pkg, cls = primary_class(java_imports, body, path)
    mirror_ns_name = mirror_ns(pkg, cls)
    out_path = mirror_path(mirror_ns_name)

    if os.path.exists(out_path):
        return False

    refer_exclude = extract_refer_exclude(content)
    mirror_content = build_mirror_content(mirror_ns_name, java_imports, body, refer_exclude)
    clojure_content = build_clojure_wrapper(clojure_ns, mirror_ns_name, body, refer_exclude)

    if dry_run:
        print(f"CREATE {out_path}")
        print(f"UPDATE {path}")
        return True

    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    with open(out_path, "w") as f:
        f.write(mirror_content)
    with open(path, "w") as f:
        f.write(clojure_content)
    print(f"OK {path}")
    return True


def main():
    dry_run = "--dry-run" in sys.argv
    paths = sorted(glob.glob(os.path.join(SRC, "clojure", "**", "*.clj"), recursive=True))
    count = sum(process_file(p, dry_run=dry_run) for p in paths)
    print(f"Processed {count} files")


if __name__ == "__main__":
    main()
