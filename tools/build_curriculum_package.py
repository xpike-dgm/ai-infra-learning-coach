#!/usr/bin/env python3
"""Builds an authored curriculum package (`curriculum_package/1`) from a content directory — 15A, CPFX-v0 / D-112.

The content directory holds the human-readable source: `package.yaml` (graph ratification and refinements over the
6C draft), `notation.yaml` (which reading constructs each Skill's lesson introduces) and one YAML file per Skill
(lesson text, alternatives, misconception catalog, items, tasks).

Nothing is trusted because it was written down. Every item's answer key is checked before it may be published as
validated (AIV-v0 §3-§8, §23-§25):

* an item with code is **executed** (CPython, isolated, with a timeout) and the key must be what the code really does;
* a choice item must have exactly one option that is true, checked by running each option where it is code;
* an item that cannot be executed must cite an authoritative reference that the independent review confirmed;
* and every item, executed or not, must carry a `pass` verdict from the independent review file.

An item failing any of these is written as `candidate` (practice at best, never mastery evidence), and a task that
presents it is written as `candidate` too. The build is deterministic: the same content gives the same bytes.

Usage:
  python tools/build_curriculum_package.py curriculum/content/15a_computing_fundamentals \
      --out android/app-wiring/src/main/assets/curriculum_package.txt \
      --report arch/15a_computing_fundamentals/content_verification.yaml
"""
from __future__ import annotations

import argparse
import copy
import json
import os
import re
import subprocess
import sys
import tempfile
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
DECOMPOSITION = ROOT / "curriculum/decomposition/6c_foundations"
PYTHON = sys.executable
TIMEOUT_SECONDS = 5
NONTERMINATION_SECONDS = 2
# 2026-10-02T12:00:00Z — the day the independent review of 15A content was recorded.
VALIDATED_AT = 1_790_942_400_000
CHOICE_KEYS = ["A", "B", "C", "D", "E"]
RUNNER = ROOT / "tools/code_test_runner.py"
# A code item's suite (15B): the learner's file, how it is checked for syntax and run, and how output is compared.
CODE_FILE = "cozum.py"
CODE_BUILD = ["{python}", "-m", "py_compile", CODE_FILE]
CODE_RUN = ["{python}", CODE_FILE]
CODE_TIMEOUT_SECONDS = 5
PYTHON_CODE = {"file": CODE_FILE, "build": CODE_BUILD, "run": CODE_RUN, "environment": "host"}
# 15C (`D-114`, user decision): C is written, built and run on Linux (WSL Ubuntu + gcc). The build checks every key and
# suite there, through the same runner the learner uses, so what is verified is what the learner will see.
GCC = ["gcc", "-std=c11", "-Wall", "-Wextra"]


def c_harness_build(harness: str, cflags: list[str] | None = None) -> list[str]:
    """15C: the build of an item that asks for a function. The course's test program (its own main) is written beside
    the learner's file; the learner's main, if any, is renamed so that only the course's runs; the two are linked. A
    missing or misnamed function is an undefined reference: the build fails and no test passes."""
    # 15D: a package may build with more flags (sanitizers); they must reach every compile and the link.
    flags = " ".join(cflags) if cflags else " ".join(GCC)
    link = ("gcc " + " ".join(f for f in cflags if f.startswith("-fsanitize") or f == "-g") + " cozum.o ders_test.o -o cozum") if cflags \
        else "gcc cozum.o ders_test.o -o cozum"
    script = ("cat > ders_test.c <<'DERS_TEST_EOF'\n" + harness.strip("\n") + "\nDERS_TEST_EOF\n"
              + flags + " -Dmain=ogrenci_main -c cozum.c -o cozum.o && "
              + flags + " -c ders_test.c -o ders_test.o && " + link)
    return ["bash", "-c", script]


def wsl_path(path: Path | str) -> str:
    """C:\\x\\y -> /mnt/c/x/y (the WSL view of a Windows path)."""
    text = str(Path(path).resolve()).replace("\\", "/")
    return f"/mnt/{text[0].lower()}{text[2:]}" if len(text) > 1 and text[1] == ":" else text


# Starting WSL and gcc can take seconds on a loaded machine; no C key or lesson claim is about time (none loops on
# purpose), so the limit only has to stop a stuck call. A shorter one made a lesson check fail under load (15C).
LINUX_TIMEOUT_SECONDS = 60


def in_linux(argv: list[str], cwd: Path | str, stdin: str = "", timeout: float = LINUX_TIMEOUT_SECONDS) -> tuple[str, int, str, str]:
    """Runs [argv] in Linux: through WSL on Windows, directly elsewhere. Returns (status, exit code, stdout, stderr)."""
    full = (["wsl.exe", "--cd", wsl_path(cwd), "-e"] + argv) if os.name == "nt" else argv
    try:
        done = subprocess.run(full, cwd=None if os.name == "nt" else cwd, capture_output=True, input=stdin.encode("utf-8"),
                              timeout=timeout)
    except subprocess.TimeoutExpired:
        return "timeout", -1, "", ""
    out, err = done.stdout.decode("utf-8", "replace"), done.stderr.decode("utf-8", "replace")
    return ("ok" if done.returncode == 0 else "error"), done.returncode, out, err


def c_stages(code: str, stdin: str = "") -> tuple[str, str]:
    """Builds and runs a C program the way the lessons do. Returns (stage, stdout): the first stage that fails —
    compile, link, run — or "ok", and what the program printed."""
    with tempfile.TemporaryDirectory() as d:
        (Path(d) / "program.c").write_text(code, encoding="utf-8", newline="\n")
        status, _, _, _ = in_linux(GCC + ["-c", "program.c", "-o", "program.o"], d)
        if status != "ok":
            return "compile", ""
        status, _, _, _ = in_linux(["gcc", "program.o", "-o", "program"], d)
        if status != "ok":
            return "link", ""
        status, _, out, _ = in_linux(["./program"], d, stdin=stdin)
        return ("ok" if status == "ok" else "run"), out


# 15D (`D-116`): a use of memory that no longer exists is undefined behaviour — its output proves nothing — but
# AddressSanitizer reports it deterministically. The kind of report (or "ok") is what a key about lifetime is checked against.
SANITIZE = ["-g", "-fsanitize=address,undefined", "-fno-omit-frame-pointer", "-fno-sanitize-recover=all"]
ASAN_RUN = ["env", "ASAN_OPTIONS=detect_stack_use_after_return=1:detect_leaks=0"]


def c_sanitized(code: str, stdin: str = "") -> tuple[str, str]:
    """Builds the program with AddressSanitizer and UndefinedBehaviorSanitizer and runs it in Linux. Returns (finding,
    stdout): the sanitizer's error kind (e.g. "stack-use-after-return", "heap-use-after-free"), "compile" if it does
    not build, or "ok"."""
    with tempfile.TemporaryDirectory() as d:
        (Path(d) / "program.c").write_text(code, encoding="utf-8", newline="\n")
        status, _, _, _ = in_linux(GCC + SANITIZE + ["-o", "program", "program.c"], d)
        if status != "ok":
            return "compile", ""
        status, _, out, err = in_linux(ASAN_RUN + ["./program"], d, stdin=stdin)
    for line in err.splitlines():
        if "ERROR: AddressSanitizer:" in line:
            return line.split("ERROR: AddressSanitizer:", 1)[1].split()[0], out
        if "runtime error:" in line:
            return "undefined-behavior", out
    return ("ok" if status == "ok" else "run"), out


# 15E (`D-118`): a package may give every shell run the same hidden first lines — a fixed Git identity and no user or
# system configuration — so that a key never depends on the machine that checks it. Earlier packages have none.
SHELL_PRELUDE = ""


def run_shell(script: str) -> tuple[str, str]:
    """Runs a shell script in an empty directory in Linux. Returns (status, stdout)."""
    with tempfile.TemporaryDirectory() as d:
        status, _, out, _ = in_linux(["bash", "-c", SHELL_PRELUDE + script], d)
    return status, out


class BuildError(Exception):
    pass


def load(path: Path):
    with open(path, encoding="utf-8") as f:
        return yaml.safe_load(f)


# ------------------------------------------------------------------------------------------------ execution

def run_python(code: str, cwd: str | None = None, timeout: float = TIMEOUT_SECONDS, stdin: str = "") -> tuple[str, str, str]:
    """Runs [code] in an isolated CPython (`-I`: no user site, no environment). Returns (status, stdout, stderr)."""
    try:
        done = subprocess.run([PYTHON, "-I", "-X", "utf8", "-c", code], capture_output=True, text=True, timeout=timeout,
                              cwd=cwd, input=stdin, encoding="utf-8", env={"PYTHONIOENCODING": "utf-8", "SYSTEMROOT": os.environ.get("SYSTEMROOT", "")})
    except subprocess.TimeoutExpired:
        return "timeout", "", ""
    return ("ok" if done.returncode == 0 else "error"), done.stdout, done.stderr


def norm(text: str) -> str:
    return "\n".join(line.rstrip() for line in text.strip().splitlines())


def verify_item(item: dict) -> tuple[bool, str]:
    """Checks the item's key against what really happens. Returns (verified, how)."""
    v = item.get("verify") or {}
    mode = v.get("mode")
    key = str(item["answer"]).strip()
    options = {k: str(t) for k, t in (item.get("options") or {}).items()}

    # An option may combine an outcome with its reason; `outcomes` then names the outcome each option claims, and
    # it is the outcome that is checked against what really happens.
    claims = {k: str(t) for k, t in (v.get("outcomes") or options).items()}

    def keyed_option_matches(actual: str) -> tuple[bool, str]:
        hits = [k for k, text in claims.items() if norm(text) == norm(actual)]
        if hits != [key]:
            return False, f"true value {actual!r} matches options {hits}, key is {key}"
        return True, f"executed; true value {actual!r} is option {key} and no other"

    if mode == "stdout":
        status, out, err = run_python(item["code"])
        # `expect_error`: the program is meant to fail, and what it printed before failing is the answer.
        if status != ("error" if v.get("expect_error") else "ok"):
            return False, f"code did not run as declared: {status} {err.strip()[-200:]}"
        if v.get("last_line"):
            out = (out.strip().splitlines() or [""])[-1]
        if options:
            return keyed_option_matches(out)
        if norm(out) != norm(key):
            return False, f"code prints {norm(out)!r}, key is {key!r}"
        return True, "executed; the key is exactly what the code prints"

    if mode == "probe":
        lines = item["code"].rstrip("\n").split("\n")
        upto = v.get("upto")
        body = "\n".join(lines[:upto] if upto else lines)
        status, out, err = run_python(body + "\n" + v["probe"])
        if status != "ok":
            return False, f"probe did not run: {status} {err.strip()[-200:]}"
        return keyed_option_matches(out) if options else ((norm(out) == norm(key)), f"probe prints {norm(out)!r}")

    if mode == "termination":
        status, out, err = run_python(item["code"], timeout=NONTERMINATION_SECONDS)
        terminates = status == "ok"
        if status == "error":
            return False, f"code raised: {err.strip()[-200:]}"
        claim = v["keyed_terminates"]
        if claim != terminates:
            return False, f"key claims terminates={claim}, execution says terminates={terminates}"
        if "iterations_probe" in v and terminates:
            s2, o2, e2 = run_python(item["code"] + "\n" + v["iterations_probe"])
            if norm(o2) != norm(str(v["iterations"])):
                return False, f"iteration probe prints {o2!r}, expected {v['iterations']}"
        return True, f"executed with a {NONTERMINATION_SECONDS}s limit; terminates={terminates} as keyed"

    if mode == "behaviour":
        # The program, as a template over inputs, is run for every case; exactly the keyed option's reference agrees.
        cases = v["cases"]
        refs = v["refs"]
        actual = []
        for case in cases:
            code = v["template"].format(**case)
            status, out, err = run_python(code)
            if status != "ok":
                return False, f"template failed on {case}: {err.strip()[-200:]}"
            actual.append(norm(out))
        agree = []
        for k, ref in refs.items():
            expected = []
            for case in cases:
                status, out, err = run_python(f"f = {ref}\nprint(f(**{case!r}))")
                if status != "ok":
                    return False, f"reference {k} failed: {err.strip()[-200:]}"
                expected.append(norm(out))
            if expected == actual:
                agree.append(k)
        if agree != [key]:
            return False, f"options agreeing with the program on all {len(cases)} cases: {agree}; key {key}"
        return True, f"executed on {len(cases)} cases; only option {key} describes the program"

    if mode == "functions":
        # Each option is a function; exactly the keyed one meets the specification on every case.
        spec = v["spec"]
        cases = v["cases"]
        name = v["name"]
        passing = []
        for k, src in (v.get("sources") or options).items():
            ok = True
            for case in cases:
                prog = f"{src}\n{spec}\nimport sys\nr = {name}(*{case!r})\ne = spec(*{case!r})\nprint(r == e and type(r) == type(e))"
                status, out, err = run_python(prog)
                if status != "ok" or out.strip() != "True":
                    ok = False
                    break
            if ok:
                passing.append(k)
        if passing != [key]:
            return False, f"options meeting the specification: {passing}; key {key}"
        return True, f"executed on {len(cases)} cases; only option {key} meets the specification"

    if mode == "detects":
        # Each option is a test input; exactly the keyed one tells the faulty function from the correct one.
        detect = []
        for k, args in v["inputs"].items():
            prog = (f"{v['correct']}\ncorrect = {v['name']}\n{v['faulty']}\nfaulty = {v['name']}\n"
                    f"print(correct(*{args!r}) != faulty(*{args!r}))")
            status, out, err = run_python(prog)
            if status != "ok":
                return False, f"input {k} did not run: {err.strip()[-200:]}"
            if out.strip() == "True":
                detect.append(k)
        if detect != [key]:
            return False, f"inputs that detect the fault: {detect}; key {key}"
        return True, f"executed; only input {key} separates the faulty function from the correct one"

    if mode == "fault":
        # The program misbehaves; replacing exactly the keyed line with the fix makes it behave, and the symptom
        # stated in the prompt is what the faulty program really prints.
        status, out, err = run_python(item["code"])
        if status != "ok" or norm(out) != norm(str(v["observed"])):
            return False, f"faulty code prints {norm(out)!r} ({status}), prompt says {v['observed']!r}"
        lines = item["code"].rstrip("\n").split("\n")
        line_no = v["line_options"][key]
        fixed = list(lines)
        indent = re.match(r"\s*", fixed[line_no - 1]).group(0)
        fixed[line_no - 1] = indent + v["fix"].strip()
        status, out2, err = run_python("\n".join(fixed))
        if status != "ok" or norm(out2) != norm(str(v["intended"])):
            return False, f"fixed code prints {norm(out2)!r}, intended {v['intended']!r}"
        if norm(out) == norm(str(v["intended"])):
            return False, "the faulty code already behaves as intended"
        return True, f"executed; the symptom is real and fixing line {line_no} alone gives the intended output"

    if mode == "script":
        # A verification script (run in an empty temporary directory) prints the true answer.
        with tempfile.TemporaryDirectory() as d:
            status, out, err = run_python(v["script"], cwd=d)
        if status != "ok":
            return False, f"script failed: {err.strip()[-300:]}"
        return keyed_option_matches(out) if options else ((norm(out) == norm(key)), f"script prints {norm(out)!r}")

    if mode == "fix_terminates":
        # Each option replaces one line of a non-terminating loop; exactly the keyed replacement makes it terminate.
        status, out, err = run_python(item["code"], timeout=NONTERMINATION_SECONDS)
        if status != "timeout":
            return False, f"the program shown is meant not to terminate, but it ended ({status})"
        lines = item["code"].rstrip("\n").split("\n")
        indent = re.match(r"\s*", lines[v["line"] - 1]).group(0)
        ends = []
        for k, replacement in v["replacements"].items():
            changed = list(lines)
            changed[v["line"] - 1] = indent + replacement.strip()
            status, out, err = run_python("\n".join(changed), timeout=NONTERMINATION_SECONDS)
            if status == "error":
                return False, f"option {k} raised: {err.strip()[-200:]}"
            if status == "ok":
                ends.append(k)
        if ends != [key]:
            return False, f"replacements that terminate: {ends}; key {key}"
        return True, f"executed with a {NONTERMINATION_SECONDS}s limit; only replacement {key} terminates"

    if mode == "which_input":
        # Each option is a starting assignment; exactly the keyed one makes the program print the target.
        hits = []
        for k, assignment in v["assignments"].items():
            status, out, err = run_python(assignment + "\n" + item["code"])
            if status != "ok":
                return False, f"option {k} did not run: {err.strip()[-200:]}"
            if norm(out) == norm(str(v["target"])):
                hits.append(k)
        if hits != [key]:
            return False, f"starting values that print {v['target']!r}: {hits}; key {key}"
        return True, f"executed for every option; only option {key} prints {v['target']!r}"

    if mode == "traceback":
        # The program is run as program.py and must fail. A traceback shown in the prompt must be the one Python
        # really prints (the caret lines, which differ between Python versions, are left out); the key is checked
        # against the part of the real traceback the item asks for (15B).
        with tempfile.TemporaryDirectory() as d:
            (Path(d) / "program.py").write_text(item["code"], encoding="utf-8")
            try:
                done = subprocess.run([PYTHON, "-I", "-X", "utf8", "program.py"], capture_output=True, text=True, cwd=d,
                                      timeout=TIMEOUT_SECONDS, encoding="utf-8",
                                      env={"PYTHONIOENCODING": "utf-8", "SYSTEMROOT": os.environ.get("SYSTEMROOT", "")})
            except subprocess.TimeoutExpired:
                return False, "the program did not end"
            err = done.stderr.replace(str(Path(d) / "program.py"), "program.py")
        if done.returncode == 0 or "Traceback (most recent call last):" not in err:
            return False, "the program is meant to fail with a traceback, but it did not"
        tb = err[err.index("Traceback (most recent call last):"):]
        tb = "\n".join(line.rstrip() for line in tb.strip().splitlines() if not re.fullmatch(r"\s*[~^]+\s*", line))
        if v.get("shown", True) and tb not in norm(item["prompt"]):
            return False, f"the prompt does not show the real traceback:\n{tb}"
        frames = re.findall(r'File "program\.py", line (\d+), in (\S+)', tb)
        last = tb.splitlines()[-1]
        error_type, _, message = last.partition(": ")
        actual = {
            "type": error_type,
            "line": frames[-1][0],
            "function_line": f"{frames[-1][1]} {frames[-1][0]}",
            "module_line": frames[0][0],
            "message": message,
        }[v["ask"]]
        if options:
            return keyed_option_matches(actual)
        if norm(actual) != norm(key):
            return False, f"the traceback gives {actual!r} for {v['ask']}, key is {key!r}"
        return True, f"executed; the traceback is real and its {v['ask']} is the key"

    if mode == "c_stdout":
        # 15C: the program is built with gcc and run in Linux; what it prints (for the given input) is the key.
        stage, out = c_stages(item["code"], v.get("stdin", ""))
        if stage != v.get("expect_stage", "ok"):
            return False, f"the program stops at {stage!r}, the item says {v.get('expect_stage', 'ok')!r}"
        if options:
            return keyed_option_matches(out)
        if norm(out) != norm(key):
            return False, f"program prints {norm(out)!r}, key is {key!r}"
        return True, "built with gcc and run in Linux; the key is exactly what the program prints"

    if mode == "c_sanitize":
        # 15D: what AddressSanitizer reports when the program runs — a lifetime error by its kind, or "ok".
        finding, out = c_sanitized(item["code"], v.get("stdin", ""))
        if "prints" in v and finding == "ok" and norm(out) != norm(str(v["prints"])):
            return False, f"program prints {norm(out)!r}, the item says {v['prints']!r}"
        if options:
            return keyed_option_matches(finding)
        return (norm(finding) == norm(key)), f"built with AddressSanitizer in Linux; the finding is {finding!r}"

    if mode == "c_stage":
        # 15C: which stage the program first fails at — compile, link, run — or ok. Only the keyed option names it.
        stage, _ = c_stages(item["code"], v.get("stdin", ""))
        if options:
            return keyed_option_matches(stage)
        return (norm(stage) == norm(key)), f"built with gcc and run in Linux; the program stops at {stage!r}"

    if mode == "shell":
        # 15C: the commands are run in an empty directory in Linux (bash); what they print is the key.
        # 15E: without a separate script, the commands the item shows are what runs, followed by an optional probe
        # (e.g. basename "$PWD") that prints what the question asks about.
        if "script" in v:
            script = v["script"]
        elif item.get("code"):
            script = item["code"].rstrip("\n") + "\n" + v.get("probe", "")
        else:
            return False, "a shell item names the commands it runs (code) or a script"
        status, out = run_shell(script)
        if status != "ok":
            return False, f"the shell script failed: {norm(out)!r}"
        if v.get("last_line"):
            out = (out.strip().splitlines() or [""])[-1]
        if options:
            return keyed_option_matches(out)
        if norm(out) != norm(key):
            return False, f"the commands print {norm(out)!r}, key is {key!r}"
        return True, "run in bash in an empty directory in Linux; the key is exactly what the commands print"

    if mode == "reference":
        if not v.get("reference") or not v.get("claim"):
            return False, "a reference item names its source and the claim it checks"
        return True, f"reference-grounded: {v['reference']}"

    if mode == "rubric":
        return True, "open response; judged by its rubric (OREX-v0), provisional at most"

    return False, f"unknown verify mode {mode!r}"


def run_suite(suite: dict, solution: str, code: dict = PYTHON_CODE) -> dict[str, str]:
    """Runs the course's real runner (14D) against [solution] in an empty directory; returns each test's status.
    A Linux package (15C) runs the runner itself in Linux, with Linux's python3, as the learner does."""
    with tempfile.TemporaryDirectory() as d:
        (Path(d) / "suite.json").write_text(json.dumps(suite, ensure_ascii=False), encoding="utf-8")
        (Path(d) / code["file"]).write_text(solution, encoding="utf-8", newline="\n")
        if code.get("environment") == "linux":
            _, _, stdout, _ = in_linux(["python3", wsl_path(RUNNER) if os.name == "nt" else str(RUNNER), "suite.json", "--dir", "."],
                                       d, timeout=300)
        else:
            done = subprocess.run([PYTHON, str(RUNNER), str(Path(d) / "suite.json"), "--dir", d], capture_output=True, timeout=300)
            stdout = done.stdout.decode("utf-8")
    lines = stdout.splitlines()
    statuses = {}
    for line in lines:
        if line.startswith("build: "):
            statuses["__build__"] = line.split(": ", 1)[1]
        if line.startswith("test: "):
            test_id, status = line[len("test: "):].rsplit(" ", 1)
            statuses[test_id] = status
    return statuses


def verify_suite(suite: dict, item: dict, code: dict = PYTHON_CODE) -> tuple[bool, str]:
    """The reference passes every test; every plausible wrong solution fails at least one (the tests can tell)."""
    ids = [t["id"] for t in suite["tests"]]
    ref = run_suite(suite, item["reference"], code)
    if ref.get("__build__") != "ok" or any(ref.get(i) != "passed" for i in ids):
        return False, f"the reference solution does not pass its own suite: {ref}"
    for n, wrong in enumerate(item.get("wrong", []), 1):
        got = run_suite(suite, wrong, code)
        if all(got.get(i) == "passed" for i in ids) and got.get("__build__") == "ok":
            return False, f"wrong solution {n} passes every test: the suite cannot tell it from a correct one"
    if len(item.get("wrong", [])) < 2:
        return False, "a code item names at least two plausible wrong solutions the suite must catch"
    return True, f"executed with the course runner; the reference passes {len(ids)} tests and each of {len(item['wrong'])} wrong solutions fails at least one"


# ------------------------------------------------------------------------------------------------ notation

def english_words(text: str) -> set[str]:
    """15F: the English words a learner has to read, lower-cased. A literal is not vocabulary: a `code literal` in
    backticks, a 'quoted' or "quoted" name (as tools quote file names and identifiers), any token with a character that
    is not a letter (notes.txt, -h, 404, main.c), and a mixed-case identifier such as NameError (a capital after a small
    letter). A capitalised (Save) or all-capital (ERROR) label is a word. An apostrophe inside a word (don't) belongs to
    the word."""
    text = re.sub(r"`[^`\n]*`", " ", text)
    words: set[str] = set()
    for raw in text.split():
        bare = raw.rstrip(".,;:!?)]}")
        quoted = raw[:1] in "'\"" or bare[-1:] in "'\""
        token = raw.strip("()[]{}<>,.;:!?\"'")
        if quoted or not re.fullmatch(r"[A-Za-z]+(?:'[A-Za-z]+)?", token) or re.search(r"[a-z][A-Z]", token):
            continue
        words.add(token.lower())
    return words


def constructs_used(code: str, notation: dict) -> set[str]:
    # What is inside a string literal is text, not code: print("bool('False')") uses no boolean logic (15B).
    code = re.sub(r'"[^"\n]*"', '""', code)
    # 15C: a notation may name more text that is not code to read (an #include line, a character literal).
    for pattern in notation.get("strip", []):
        code = re.sub(pattern, "", code, flags=re.M)
    used = set()
    for construct, spec in notation["constructs"].items():
        if re.search(spec["pattern"], code, re.M):
            used.add(construct)
    return used


# ------------------------------------------------------------------------------------------------ package text

# 15C (`D-114`): a package whose content shows C code doubles every backslash in a multi-line value, so a literal
# backslash-n survives (PackageFormat.unescape reads it back). Earlier packages keep the rule they were built with.
ESCAPE_BACKSLASH = False


def esc(text: str, multiline: bool = False) -> str:
    """One package line per value: a line break becomes a backslash-n (PackageFormat reads it back)."""
    text = text.strip("\n")
    if ESCAPE_BACKSLASH:
        if not multiline and "\\" in text:
            raise BuildError(f"a single-line value is not unescaped by the app and may not contain a backslash: {text[:60]!r}")
        return text.replace("\r", "").replace("\\", "\\\\").replace("\n", "\\n")
    if "\\n" in text:
        raise BuildError(f"content contains a literal backslash-n, which the format reads as a line break: {text[:60]!r}")
    return text.replace("\r", "").replace("\n", "\\n")


def section(name: str, values: list[tuple[str, object]]) -> str:
    lines = [f"[{name}]"]
    for k, v in values:
        if isinstance(v, bool):
            v = "true" if v else "false"
        elif isinstance(v, (list, tuple)):
            v = ",".join(str(x) for x in v)
        elif v is None:
            v = ""
        lines.append(f"{k}={v}")
    return "\n".join(lines)


def pin(ref: str) -> str:
    return f"{ref}@v1"


# 15G (`D-120`): the month's transfer role and the `QAB-v0` §17 profiles that can carry it — a context built from
# another Topic. `same_context`/`near_context` are not transfer, and `novel_application` is not cross-topic.
TRANSFER_ROLE = "cross_topic_transfer"
CROSS_TOPIC_PROFILES = {"cross_topic_context", "integrated_system_context"}


def form_problems(it: dict, objective: dict) -> list[str]:
    """15G (`D-120`): an item's form must be able to produce the evidence it is stored as. The evidence type is the item's
    own declaration or its Objective's required direct type; a chosen option is never authored code, a hands-on result
    or a written production, so a choice item cannot claim one (the store would record the wrong kind of evidence)."""
    evidence = it.get("evidence_type") or objective.get("required_direct_type") or objective["direct_evidence_types"][0]
    problems = []
    if it.get("evidence_type") and it["evidence_type"] not in objective.get("acceptable_evidence_types", []):
        problems.append(f"evidence type {it['evidence_type']!r} is not one the Objective accepts")
    if evidence == "authored_code" and "suite" not in it:
        problems.append("authored_code evidence comes only from a program the learner writes (a suite)")
    if evidence == "hands_on_system_task" and "suite" not in it and not it.get("hands_on"):
        problems.append("hands_on_system_task evidence comes only from work done at the computer (hands_on or a suite)")
    if evidence == "written_or_spoken_production" and it.get("options"):
        problems.append("written_or_spoken_production evidence is never a chosen option")
    return problems


def transfer_problems(it: dict, skill: str, declared: set[str], topic_of: dict[str, str]) -> list[str]:
    """What keeps a transfer claim from being checked (`AIV-v0` §16). The review decides whether the structure really
    changed; the build checks what it can: the profile, a named context, and a context that really comes from another
    Topic — a declared Skill, gated like every requirement, whose lesson lives elsewhere."""
    problems = []
    if it.get("transfer_profile") not in CROSS_TOPIC_PROFILES:
        problems.append(f"transfer profile {it.get('transfer_profile')!r} is not a cross-topic context")
    if not it.get("context_family"):
        problems.append("a transfer item names its context family")
    if skill in declared:
        problems.append("a transfer item does not require its own Skill")
    if not any(topic_of.get(r) and topic_of.get(r) != topic_of.get(skill) for r in declared):
        problems.append("a cross-topic context needs a declared Skill from another Topic")
    if it.get("in_lesson") or it.get("difficulty") != "transfer_integration":
        problems.append("a transfer item is a transfer_integration item outside the lesson")
    return problems


def source_check_harness(language: str, checks: dict) -> str:
    """15H (`D-121`): the harness of a code item whose answer is one fixed output, written from what the item declares.

    A fixed output can be printed directly. Where producing it is not the construct, the suite must also see how it was
    produced, so the course's harness runs the learner's program — as a program, `python cozum.py` would, so a file
    guarded by `if __name__ == "__main__":` runs too, and a program that ends with sys.exit() keeps its names
    (independent review, 15H) — and prints, after what it printed, one
    line per declared check. For Python: whether a value the program must compute is written in the source (as a number
    or inside a text), the final values of named variables, whether each named variable is updated from its own value
    (`x += ...`, `x = x - ...`), and whether a variable is bound from another (`kopya = ilk`). For C: whether the
    constructs the prompt asks for (a declaration, a cast, an expression, the loop) are there, whether a value the program
    must produce is written inside a text, and whether a declared variable goes unused or is set and never read. The
    harness is the course's code and is not scanned; it decides nothing about the learner, and the expected output of
    each test names every line, so a wrong line is a failed test."""
    forbidden = [str(v) for v in checks.get("not_in_source", [])]
    if language == "python":
        variables = [str(v) for v in checks.get("variables", [])]
        updated = [str(v) for v in checks.get("updated", [])]
        bound = {str(k): str(v) for k, v in (checks.get("bound_from") or {}).items()}
        lines = [
            "import ast, contextlib, io, re, tokenize",
            'with open("cozum.py", "rb") as f:',
            "    kaynak = f.read()",
            # Run as `python cozum.py` would, into a namespace the harness keeps even if the program calls sys.exit().
            'adlar = {"__name__": "__main__", "__file__": "cozum.py", "__builtins__": __builtins__}',
            "with contextlib.redirect_stdout(io.StringIO()) as cikti:",
            "    try:",
            '        exec(compile(kaynak, "cozum.py", "exec"), adlar)',
            "    except SystemExit:",
            "        pass",
            "print(cikti.getvalue().rstrip())",
        ]
        if forbidden:
            lines += [
                f"yasak = {forbidden!r}",
                "jetonlar = list(tokenize.tokenize(io.BytesIO(kaynak).readline))",
                'metin = {tokenize.STRING, getattr(tokenize, "FSTRING_MIDDLE", -1), getattr(tokenize, "TSTRING_MIDDLE", -1)}',
                "def yazili(deger):",
                "    for j in jetonlar:",
                "        if j.type == tokenize.NUMBER and j.string == deger:",
                "            return True",
                '        if j.type in metin and re.search(r"(?<![0-9.])" + re.escape(deger) + r"(?![0-9])", j.string):',
                "            return True",
                "    return False",
                'print("sonuc kaynakta yazili:", "evet" if any(yazili(d) for d in yasak) else "hayir")',
            ]
        required = [str(r) for r in checks.get("required", [])]
        if required:
            # Looked for in the code only: every comment and every text is blanked first, so a pattern written inside a
            # string ("2 * (3 + 4)") is not the construct.
            lines += [
                f"istenen = {required!r}",
                "kod_satirlari = kaynak.decode('utf-8-sig').splitlines()",
                "for j in sorted(tokenize.tokenize(io.BytesIO(kaynak).readline), key=lambda j: j.start, reverse=True):",
                '    if j.type in (tokenize.COMMENT, tokenize.STRING, getattr(tokenize, "FSTRING_MIDDLE", -1)) and j.start[0] == j.end[0]:',
                "        s = kod_satirlari[j.start[0] - 1]",
                "        kod_satirlari[j.start[0] - 1] = s[:j.start[1]] + ' ' * (j.end[1] - j.start[1]) + s[j.end[1]:]",
                "kod = chr(10).join(kod_satirlari)",
                'print("istenen yapilar:", "tamam" if all(re.search(r, kod) for r in istenen) else "eksik")',
            ]
        if variables:
            lines += [f'print("degerler:", *[adlar.get(a, "<yok>") for a in {variables!r}])']
        if updated or bound:
            lines += [
                "agac = ast.parse(kaynak)",
                "def ad_icinde(dugum, ad):",
                "    return any(isinstance(n, ast.Name) and n.id == ad for n in ast.walk(dugum))",
                "def guncellenir(ad):",
                "    for n in ast.walk(agac):",
                "        if isinstance(n, ast.AugAssign) and isinstance(n.target, ast.Name) and n.target.id == ad:",
                "            return True",
                "        if isinstance(n, ast.Assign) and any(isinstance(t, ast.Name) and t.id == ad for t in n.targets) and ad_icinde(n.value, ad):",
                "            return True",
                "    return False",
                "def baglanir(hedef, kaynak_ad):",
                "    return any(isinstance(n, ast.Assign) and any(isinstance(t, ast.Name) and t.id == hedef for t in n.targets)",
                "               and isinstance(n.value, ast.Name) and n.value.id == kaynak_ad for n in ast.walk(agac))",
            ]
        if updated:
            lines += [f'print("kendi degerinden guncellenir:", "evet" if all(guncellenir(a) for a in {updated!r}) else "hayir")']
        if bound:
            lines += [f'print("degerden kopyalanir:", "evet" if all(baglanir(h, k) for h, k in {bound!r}.items()) else "hayir")']
        return "\n".join(lines) + "\n"
    if language == "c":
        required = [str(d) for d in checks.get("required", [])]
        return "\n".join([
            "import re, subprocess, sys",
            f"istenen = {required!r}",
            f"yasak = {forbidden!r}",
            'with open("cozum.c", encoding="utf-8") as f:',
            r'    kod = re.sub(r"//[^\n]*|/\*.*?\*/", "", f.read(), flags=re.S)',
            r'''yazilar = re.findall(r'"(?:\\.|[^"\\\n])*"', kod)''',
            'cikti = subprocess.run(["./cozum"], input=sys.stdin.read(), capture_output=True, text=True).stdout',
            'kullanilmayan = subprocess.run(["gcc", "-std=c11", "-fsyntax-only", "-Werror=unused-variable",',
            '                                "-Werror=unused-but-set-variable", "cozum.c"], capture_output=True).returncode != 0',
            # A required construct is looked for in the code, never inside a text ("2 * (3 + 4)" in a format string).
            r'''kod_yazisiz = re.sub(r'"(?:\\.|[^"\\\n])*"', '""', kod)''',
            "eksik = [b for b in istenen if not re.search(b, kod_yazisiz)]",
            r'gomulu = [d for d in yasak if any(re.search(r"(?<![0-9.])" + re.escape(d) + r"(?![0-9])", y) for y in yazilar)]',
            "print(cikti.rstrip())",
            'print("istenen yapilar:", "tamam" if not eksik else "eksik")',
            'print("sonuc yazida:", "evet" if gomulu else "hayir")',
            'print("kullanilmayan degisken:", "var" if kullanilmayan else "yok")',
        ]) + "\n"
    raise BuildError(f"no source checks for {language!r}")


def constant_program(code_file: str, expected: str) -> str:
    """15H (`D-121`): the program that only prints what the first test expects. A suite it passes cannot tell a program
    that does the work from one that prints the answer."""
    if code_file.endswith(".c"):
        literal = expected.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n").replace("%", "%%")
        return f'#include <stdio.h>\n\nint main(void) {{\n    printf("{literal}");\n    return 0;\n}}\n'
    return f"import sys\nsys.stdout.write({expected!r})\n"


# 15H (`D-121`): keys of a revision that describe the revision itself, never the item it publishes.
REVISION_META = {"revises", "reason", "relabel_from"}


# ------------------------------------------------------------------------------------------------ build

def build(content_dir: Path, partial: bool = False, supplement: dict | None = None) -> tuple[str, dict]:
    """Builds one package. With [supplement] (15G, `D-120`) it builds nothing of the package itself: the graph, the
    lessons and the items already published stay where they are, and only what the supplement adds to this content
    directory's Objectives is emitted — new items (checked by this directory's notation, lexicon and runner exactly as
    its own were), new versions of the tasks whose item lists grow, and wrong-option misconception keys."""
    pkg = load(content_dir / "package.yaml")
    notation = load(content_dir / "notation.yaml")
    review_path = content_dir / "independent_review.yaml"
    review = load(review_path) if review_path.exists() else {}
    reviewed_items = (review or {}).get("items", {}) or {}
    reviewed_text = (review or {}).get("explanations", {}) or {}
    published_reviewed = reviewed_items
    if supplement is not None:
        reviewed_items = supplement["review"]
    # 15H (`D-121`): what an earlier supplement published to this directory's Objectives is published too — its items are
    # read with their review, and a later supplement revises them exactly as it revises the directory's own.
    earlier_added: dict[str, list[dict]] = (supplement or {}).get("earlier_items", {})
    earlier_reviewed: dict = (supplement or {}).get("earlier_review", {})
    revisions: dict[str, list[dict]] = (supplement or {}).get("revisions", {})
    teach_requires: dict[str, list[str]] = (supplement or {}).get("teach_requires", {})
    # 15H: constructs the directory's notation did not name (in, is, pass, break, as ...), and Skills a construct is
    # also taught by, apply to what this supplement adds; the published packages keep the notation they were built with.
    overlay = ((supplement or {}).get("notation_overlay") or {}).get(content_dir.name) or {}
    if overlay:
        notation = copy.deepcopy(notation)
        for construct, spec in overlay.items():
            if construct in notation["constructs"]:
                notation["constructs"][construct]["introduced_by"] = sorted(
                    set(notation["constructs"][construct]["introduced_by"]) | set(spec["introduced_by"]))
            else:
                notation["constructs"][construct] = spec
    fixed_output = (supplement or {}).get("fixed_output") or {}

    skills6c ={s["skill_id_candidate"]: s for s in load(DECOMPOSITION / "skills.yaml")}
    objectives6c = {o["objective_id_candidate"]: o for o in load(DECOMPOSITION / "objectives.yaml")}
    edges6c = load(DECOMPOSITION / "prerequisite_edges.yaml")
    links6c = load(DECOMPOSITION / "topic_skill_links.yaml")
    org6c = {o["logical_id_candidate"]: o for o in load(DECOMPOSITION / "organization_entities.yaml")}

    skill_ids = [s["id"] for s in pkg["skills"]]
    skill_files = {}
    for path in sorted((content_dir / "skills").glob("*.yaml")):
        doc = load(path)
        skill_files[doc["skill"]] = (path, doc)
    missing = [s for s in skill_ids if s not in skill_files]
    if missing and not partial:
        raise BuildError(f"skills without content: {missing}")
    if partial:
        # Authoring aid only: build what exists. A partial package is never shipped (`--out` is refused).
        pkg["skills"] = [s for s in pkg["skills"] if s["id"] in skill_files]
        skill_ids = [s["id"] for s in pkg["skills"]]

    # 15B (`D-113`): a later package builds on the packages before it. Their Skills are already published, so edges from
    # them are carried here, their topics are referenced and never declared again, and their lessons count as taught.
    earlier_skills: set[str] = set()
    for rel in pkg.get("builds_on", []):
        earlier_skills |= {s["id"] for s in load(ROOT / rel / "package.yaml")["skills"]}
    earlier_topics = {skills6c[s]["primary_teaching_topic_id"] for s in earlier_skills}
    known = set(skill_ids) | earlier_skills
    validated_at = pkg.get("validated_at_instant", VALIDATED_AT)
    code_cfg = {**PYTHON_CODE, **pkg.get("code", {})}
    global ESCAPE_BACKSLASH, SHELL_PRELUDE
    ESCAPE_BACKSLASH = bool(pkg.get("escape_backslash", False))
    SHELL_PRELUDE = pkg.get("shell_prelude", "")
    step = pkg.get("step", "15a")
    if supplement is not None:
        # A supplement is read by today's reader, which unescapes a doubled backslash (15C), whatever rule the content
        # directory's own package was built with.
        ESCAPE_BACKSLASH = True
        validated_at = supplement["validated_at"]
        step = supplement["step"]

    hard_prereqs: dict[str, set[str]] = {s: set() for s in known}
    for e in edges6c:
        if e["target_skill_id"] in known and e["prerequisite_skill_id"] in known and e["edge_kind"] == "hard":
            hard_prereqs[e["target_skill_id"]].add(e["prerequisite_skill_id"])

    def closure(skill: str) -> set[str]:
        seen, todo = set(), [skill]
        while todo:
            s = todo.pop()
            for p in hard_prereqs.get(s, ()):
                if p not in seen:
                    seen.add(p)
                    todo.append(p)
        return seen

    introduced: dict[str, list[str]] = {c: list(spec["introduced_by"]) for c, spec in notation["constructs"].items()}

    # 15F (`D-119`): an English package names the words each lesson introduces. An English item may show only words its
    # own Skill, a hard prerequisite of it or a declared Skill has taught (EED-v0: unknown vocabulary is never a hidden
    # prerequisite); a word no lesson introduces is refused outright. Earlier packages have no lexicon.
    lexicon: dict[str, set[str]] = {}
    for by, words in (notation.get("lexicon") or {}).items():
        for w in words:
            lexicon.setdefault(str(w).lower(), set()).add(by)

    def sources_of(skill: str, declared: set[str]) -> set[str]:
        # The Skill itself, its hard prerequisites, and what the item declares it requires (with their prerequisites).
        return {skill} | closure(skill) | declared | {p for d in declared for p in closure(d)}

    def teachable(skill: str, declared: set[str]) -> set[str]:
        # The constructs taught by the Skill itself, its hard prerequisites, or what the item declares it requires.
        sources = sources_of(skill, declared)
        return {c for c, by in introduced.items() if sources & set(by)}

    out: list[str] = ["\n".join([
        "curriculum_package/1",
        "# Generated by tools/build_curriculum_package.py from " + content_dir.relative_to(ROOT).as_posix() + " — do not edit by hand.",
        f"version={pkg['version']}",
        f"source_refs={pkg['source_refs']}",
        f"provenance={pkg['provenance']}",
    ])]
    report: dict = {"package_version": pkg["version"], "items": [], "explanations": 0, "tasks": [], "failures": []}

    # -------------------------------------------------------------- graph: topics, skills, objectives, links, edges
    topics = sorted({s6["primary_teaching_topic_id"] for s6 in (skills6c[s] for s in skill_ids)} - earlier_topics)
    for t in topics:
        out.append(section("topic", [("logical_id", t), ("version", 1), ("name", org6c[t]["display_name"])]))

    for s in pkg["skills"]:
        s6 = skills6c[s["id"]]
        out.append(section("skill", [
            ("logical_id", s["id"]), ("version", 1),
            ("canonical_name", s6["canonical_name"]),
            ("capability_statement", s.get("capability_statement") or s6["capability_statement"]),
            ("lifecycle_status", "published"),
            ("capability_kind", s6["capability_kind"]),
            ("retention_profile", s6["retention_profile"]),
            ("critical_prerequisite", bool(s6["critical_prerequisite_candidate"])),
            ("source_refs", "source.repo.fbb_v0;source.repo.internal_6c_authoring;" + pkg["content_source_ref"]),
            ("provenance", pkg["provenance"]),
        ]))

    objective_parent = {}
    objective_profile = {}
    for s in pkg["skills"]:
        _, doc = skill_files[s["id"]]
        for o in doc["objectives"]:
            o6 = objectives6c[o["id"]]
            if o6["owner_skill_id"] != s["id"]:
                raise BuildError(f"{o['id']} belongs to {o6['owner_skill_id']} in 6C")
            objective_parent[o["id"]] = s["id"]
            objective_profile[o["id"]] = o
            # 15H (`D-121`): `GRE-v0` §9 gates a critical Objective on more than the store keeps (min groups and
            # families, non-basic, authored artifact, transfer); none of those fields has a column, so the mastery engine
            # would judge a critical Objective by the standard gate. Until they are stored, a critical Objective is refused.
            if o.get("criticality", o6["criticality"]) != "standard":
                raise BuildError(f"{o['id']}: a {o.get('criticality', o6['criticality'])} Objective needs GRE-v0's gate "
                                 "fields stored, and the store has none (D-121)")
            out.append(section("objective", [
                ("logical_id", o["id"]), ("version", 1), ("parent_skill", pin(s["id"])),
                ("required", o6["requirement_role"] == "required"),
                ("criticality", o.get("criticality", o6["criticality"])),
                ("acceptable_evidence_types", o["acceptable_evidence_types"]),
                ("direct_evidence_types", o["direct_evidence_types"]),
                ("required_direct_type", o.get("required_direct_type")),
            ]))
        sixc_objectives = sorted(k for k, v in objectives6c.items() if v["owner_skill_id"] == s["id"])
        if sorted(objective_parent_k for objective_parent_k, p in objective_parent.items() if p == s["id"]) != sixc_objectives:
            raise BuildError(f"{s['id']}: Objectives differ from 6C {sixc_objectives}")

    for link in links6c:
        if link["skill_id"] in skill_ids and (link["topic_id"] in topics or link["topic_id"] in earlier_topics):
            out.append(section("topic_skill", [("topic", pin(link["topic_id"])), ("skill", pin(link["skill_id"]))]))

    for e in edges6c:
        if e["target_skill_id"] in skill_ids and e["prerequisite_skill_id"] in known:
            out.append(section("prerequisite_edge", [
                ("prerequisite", pin(e["prerequisite_skill_id"])), ("target", pin(e["target_skill_id"])),
                ("edge_version", 1), ("edge_kind", e["edge_kind"]), ("reason_kind", e["reason_kind"]),
                ("strictness_profile", e["strictness_profile_ref"]), ("lifecycle_status", "published"),
                ("provenance", pkg["provenance"]),
            ]))

    if supplement is not None:
        # The graph is published; a supplement only refers to it.
        out = []

    # -------------------------------------------------------------- per Skill content
    resources, validations, items_out, keys_out, rubrics_out, explanations_out, misconceptions_out, tasks_out = ([] for _ in range(8))
    suites_out: list[str] = []
    answer_misconceptions_out: list[str] = []
    suite_files: dict[str, str] = {}
    item_status: dict[str, str] = {}
    # 15G: items reserved for a blueprint's transfer slot are never presented by a task (a task would spend them).
    transfer_only: set[str] = set()
    # 15H: each item's newest version here, and the content of that version.
    item_version: dict[str, int] = {}
    stage_items: dict[str, dict] = {}
    topic_of = {s: skills6c[s]["primary_teaching_topic_id"] for s in known}

    def answer_misconception_sections(iid: str, base: str, ns: str, it: dict, keyed: dict, slugs: set[str], report: dict,
                                      version: int = 1, carried: dict | None = None) -> list[str]:
        """15G (`D-120`, user decision): a wrong option written to follow from one catalogued misconception of the item's
        own Objective names it. Only a choice item judged by an answer key can say so, never its correct option, and
        only a mapping the independent review passed is shipped (`WAAX-v0`: a label is as strong as its evidence).

        15H (`D-121`): a revised item's new key carries the mappings the review passed for the version it replaces only
        while its options are unchanged ([carried]); a mapping of a changed option is reviewed again."""
        sections, problems = [], []
        options = it.get("options") or {}
        if not options or "suite" in it or it.get("rubric") is not None:
            problems.append("only a choice item judged by an answer key maps a wrong option")
        for option, slug in sorted(keyed.items()):
            mapping = f"{iid}:{option}" if version == 1 else f"{iid}@v{version}:{option}"
            verdict = (supplement or {}).get("mapping_review", {}).get(mapping, {}).get("verdict")
            if verdict is None and carried is not None:
                verdict = carried.get(f"{iid}:{option}", {}).get("verdict")
            if option not in options:
                problems.append(f"{mapping}: no such option")
            elif option == str(it["answer"]).strip():
                problems.append(f"{mapping}: the correct option names no misconception")
            elif slug not in slugs:
                problems.append(f"{mapping}: {slug} is not in this Objective's catalog")
            elif verdict != "pass":
                problems.append(f"{mapping}: no pass verdict from the independent review")
            else:
                sections.append(section("answer_misconception", [
                    ("key", f"answerkey.{base}.{it['slug']}@v{version}"), ("text", option),
                    ("misconception", f"misconception.{ns}.{slug}@v1")]))
        report.setdefault("answer_misconceptions", []).extend(
            {"mapping": f"{iid}:{o}" if version == 1 else f"{iid}@v{version}:{o}", "misconception": s} for o, s in sorted(keyed.items()))
        if problems:
            report["failures"].append({"item": f"{iid} wrong-option misconceptions", "problems": problems})
            return []
        return sections

    for s in pkg["skills"]:
        sid = s["id"]
        path, doc = skill_files[sid]
        ns = sid.split(".", 1)[1]  # e.g. programming.state_assignment_model
        explanation_refs: dict[str, list[str]] = {}
        item_refs: dict[str, list[str]] = {}

        for o in doc["objectives"]:
            oid = o["id"]
            action = oid.rsplit(".", 1)[1]
            base = f"{ns}.{action}"
            ex = o["explanations"]
            refs = []
            # The course's own explanation (canonical) and the written alternatives (ALEX-v0).
            for form, spec in [("canonical", {"text": ex["canonical"]})] + [(k, v) for k, v in ex.items() if k != "canonical"]:
                eid = f"explanation.{base}.{form}"
                values = [("logical_id", eid), ("version", 1), ("objective", pin(oid)), ("form", form)]
                if form != "canonical":
                    values.append(("level", spec["level"]))
                values.append(("text", esc(spec["text"], multiline=True)))
                explanations_out.append(section("explanation", values))
                refs.append(eid)
                report["explanations"] += 1
            for m in o.get("misconceptions", []):
                mid = f"misconception.{ns}.{m['slug']}"
                misconceptions_out.append(section("misconception", [
                    ("logical_id", mid), ("version", 1), ("objective", pin(oid)),
                    ("name", esc(m["name"])), ("open_question", esc(m["open_question"])),
                ]))
                eid = f"explanation.{base}.contrast_{m['slug']}"
                explanations_out.append(section("explanation", [
                    ("logical_id", eid), ("version", 1), ("objective", pin(oid)), ("form", "misconception_contrast"),
                    ("level", m["contrast"]["level"]), ("misconception", pin(mid)), ("text", esc(m["contrast"]["text"], multiline=True)),
                ]))
                refs.append(eid)
                report["explanations"] += 1
            explanation_refs[oid] = refs
            # Every code claim a lesson makes is executed too: teaching a wrong output is worse than asking about one.
            # (A supplement does not run them again: the lesson is published and its claims were checked then.)
            for n, check in enumerate(o.get("checks", []) if supplement is None else [], 1):
                lang = check.get("lang", pkg.get("lesson_checks", "python"))
                if lang == "c" and "finding" in check:
                    finding, printed = c_sanitized(check["code"], check.get("stdin", ""))
                    status = "ok" if finding == check["finding"] else finding
                elif lang == "c":
                    stage, printed = c_stages(check["code"], check.get("stdin", ""))
                    status = "ok" if stage == check.get("stage", "ok") else stage
                elif lang == "shell":
                    status, printed = run_shell(check["code"])
                else:
                    status, printed, err = run_python(check["code"])
                ok_check = status == "ok" and norm(printed) == norm(str(check["prints"]))
                report.setdefault("explanation_checks", []).append(
                    {"objective": oid, "check": n, "result": "PASS" if ok_check else "FAIL", "printed": norm(printed)})
                if not ok_check:
                    report["failures"].append({"item": f"{oid} explanation check {n}",
                                               "problems": [f"lesson claims {check['prints']!r}, code prints {norm(printed)!r} ({status})"]})

            item_refs[oid] = []
            misconception_slugs = {m["slug"] for m in o.get("misconceptions", [])}
            added = (supplement or {}).get("items", {}).get(oid, [])
            revised: list[dict] = []
            if supplement is not None:
                # The published items are what they were: their status is the one their own build gave them.
                published_here = o["items"] + earlier_added.get(oid, [])
                for it in published_here:
                    iid = f"item.{base}.{it['slug']}"
                    reviews = published_reviewed if any(it is own for own in o["items"]) else earlier_reviewed
                    item_status[iid] = "validated" if reviews.get(iid, {}).get("verdict") == "pass" else "candidate"
                    item_refs[oid].append(iid)
                    stage_items[iid] = it
                    if it.get("transfer_profile") is not None:
                        # Reserved by the supplement that published it; a later task never presents it either.
                        transfer_only.add(iid)
                    keyed = (supplement.get("misconception_options") or {}).get(iid)
                    if keyed:
                        answer_misconceptions_out.extend(
                            answer_misconception_sections(iid, base, ns, it, keyed, misconception_slugs, report))
                taken = {it["slug"] for it in published_here}
                clash = sorted(taken & {it["slug"] for it in added})
                if clash:
                    raise BuildError(f"{oid}: added items reuse published slugs {clash}")
                # 15H (`D-121`): a revision publishes the item's next version — same logical id, same variant family — and
                # the version it replaces is retired by a validation record appended after it, never edited.
                for rev in revisions.get(oid, []):
                    old = next((it for it in published_here if it["slug"] == rev["revises"]), None)
                    if old is None:
                        raise BuildError(f"{oid}: a revision of {rev['revises']}, which is not published")
                    if not rev.get("reason"):
                        raise BuildError(f"{oid}: the revision of {rev['revises']} names no reason")
                    new = copy.deepcopy(old)
                    for key in rev.get("remove", []):
                        new.pop(key, None)
                    new.update({k: copy.deepcopy(v) for k, v in rev.items() if k not in REVISION_META | {"remove"}})
                    new["_version"], new["_reason"], new["_options_unchanged"] = 2, rev["reason"], new.get("options") == old.get("options")
                    revised.append(new)
            for it in (added + revised if supplement is not None else o["items"]):
                iid = f"item.{base}.{it['slug']}"
                ver = it.get("_version", 1)
                item_version[iid] = ver
                stage_items[iid] = it
                if it.get("source_checks"):
                    # 15H: the harness is written from the declared checks, so what is checked is what the item says.
                    it["harness"] = source_check_harness("c" if code_cfg["file"].endswith(".c") else "python", it["source_checks"])
                family = f"family.{base}.{it['slug']}"
                code = it.get("code")
                rubric = it.get("rubric")
                is_code = "suite" in it
                transfer = it.get("transfer_profile")
                # 15C: a hands-on item (building a program, navigating a terminal) is done at the computer; its answer
                # is checked by a key, but the terminal and documentation are part of the work, as for code.
                at_computer = is_code or bool(it.get("hands_on"))
                # Code may stand in the code block or in the options (a choice between definitions); both are read. For a
                # code item the reference solution is read too: it is what the learner has to be able to write.
                scanned = ((code or "") + "\n" + "\n".join(str(t) for t in (it.get("options") or {}).values() if it.get("scan_options", True))
                           + "\n" + (it.get("reference") or ""))
                used = constructs_used(scanned, notation)
                declared = set(it.get("required_skills", []))
                reachable = teachable(sid, declared)
                hidden = sorted(used - reachable)
                if lexicon:
                    words = english_words(scanned)
                    allowed = sources_of(sid, declared)
                    hidden += sorted(f"unknown_word:{w}" for w in words if w not in lexicon)
                    hidden += sorted(f"word:{w}" for w in words if w in lexicon and not (lexicon[w] & allowed))
                if is_code:
                    suite = {
                        "format": "code_test_suite/1", "suite": f"codetest.{base}.{it['slug']}@v{ver}", "item": f"{iid}@v{ver}",
                        "build": {"command": c_harness_build(it["c_harness"], code_cfg.get("harness_cflags")) if it.get("c_harness") else code_cfg["build"]},
                        # A function is checked by the course's own small harness, which imports the learner's file.
                        "run": {"command": ["{python}", "-c", it["harness"].strip()] if it.get("harness") else code_cfg["run"]},
                        "timeout_seconds": CODE_TIMEOUT_SECONDS, "compare": "trim_trailing_whitespace",
                        # 15D: a package may require every test to end cleanly (a sanitizer report ends a program with an
                        # error code even when everything it printed was right).
                        "tests": [{**{k: t[k] for k in ("id", "stdin", "args", "expected_stdout", "expected_exit_code") if k in t},
                                   **({"expected_exit_code": 0} if code_cfg.get("expect_clean_exit") and "expected_exit_code" not in t else {})}
                                  for t in it["suite"]],
                    }
                    suite_files[iid if ver == 1 else f"{iid}.v{ver}"] = json.dumps(suite, ensure_ascii=False, indent=2) + "\n"
                    ok, how = verify_suite(suite, it, code_cfg)
                else:
                    ok, how = verify_item(it)
                label = iid if ver == 1 else f"{iid}@v{ver}"
                reviewed = reviewed_items.get(label, {}).get("verdict") == "pass"
                problems = form_problems(it, o)
                if not ok:
                    problems.append(how)
                if is_code and (supplement or {}).get("constant_check") and ok:
                    # 15H (`D-121`): the program that only prints the expected output must fail, unless producing that
                    # output is itself what the Objective asks for (a recorded, reviewed exemption).
                    exempt = oid in fixed_output.get("objectives", {}) or label in fixed_output.get("items", {})
                    constant = run_suite(suite, constant_program(code_cfg["file"], it["suite"][0].get("expected_stdout", "")), code_cfg)
                    if not exempt and constant.get("__build__") == "ok" and all(constant.get(t["id"]) == "passed" for t in it["suite"]):
                        problems.append("a program that only prints the expected output passes every test")
                if hidden:
                    problems.append(f"uses constructs no prerequisite teaches: {hidden}")
                if not reviewed:
                    problems.append("no pass verdict from the independent review")
                if transfer is not None:
                    problems += transfer_problems(it, sid, declared, topic_of)
                    transfer_only.add(iid)
                status = "validated" if not problems else "candidate"
                item_status[iid] = status
                mode = "suite" if is_code else (it.get("verify") or {}).get("mode")
                report["items"].append({"item": label, "status": status, "verify_mode": mode, "verification": how,
                                        "constructs": sorted(used), "hidden_prerequisites": hidden, "reviewed": reviewed,
                                        **({"revision": it["_reason"]} if ver > 1 else {})})
                if problems:
                    report["failures"].append({"item": label, "problems": problems})

                prompt = it["prompt"].strip("\n")
                if code:
                    prompt += "\n\n" + code.rstrip("\n")
                options = it.get("options") or {}
                if options:
                    prompt += "\n\n" + "\n".join(f"{k}) {options[k]}" for k in CHOICE_KEYS if k in options)
                evidence_type = it.get("evidence_type") or o["required_direct_type"] or o["direct_evidence_types"][0]
                is_rubric = rubric is not None
                validator = {
                    "reference": f"{step}/reference_grounded+independent_review",
                    "rubric": f"{step}/rubric_reviewed+independent_review",
                    "suite": f"{step}/executed_suite+independent_review",
                }.get(mode, f"{step}/executed_key+independent_review")

                resources.append(section("resource", [
                    ("logical_id", iid), ("version", ver), ("content_ref", f"item://{iid}@v{ver}"),
                    ("rubric_ref", f"rubric.{base}.{it['slug']}@v{ver}" if is_rubric else None),
                    ("evidence_type", evidence_type), ("allowed_tools_policy", "terminal_documentation" if at_computer else "none"),
                    ("variant_family_id", family), ("content_origin", "ai_generated"),
                ]))
                if ver > 1:
                    # 15H (`D-121`): the replaced version is retired by a record appended now (`LFPS-v0`: nothing published
                    # is edited). It stays readable by its pinned reference; nothing selects it again.
                    validations.append(section("validation", [
                        ("resource", f"{iid}@v{ver - 1}"), ("validated_at_instant", validated_at), ("status", "retired"),
                        ("validator", f"{step}/superseded_by_v{ver}:{it['_reason']}+independent_review"), ("origin", "ai_generated"),
                    ]))
                validations.append(section("validation", [
                    ("resource", f"{iid}@v{ver}"), ("validated_at_instant", validated_at), ("status", status),
                    ("validator", validator), ("origin", "ai_generated"),
                ]))
                difficulty = it["difficulty"]
                roles = list(notation["roles"]["all"])
                # 15G (`D-120`, `AIV-v0` §16): a transfer role is a claim about the context, not the difficulty. A
                # supplement gives it only to an item whose cross-topic context is declared and checked; a harder item
                # of the same lesson is a harder item. (The published packages keep the roles they were built with.)
                if difficulty == "transfer_integration" and supplement is None:
                    roles += notation["roles"]["transfer"]
                if s.get("critical"):
                    roles += notation["roles"]["critical"]
                scopes = ["daily_micro", "weekly_blueprint", "monthly_capability"]
                if transfer is not None:
                    # Reserved for the month's transfer slot (`MCA-v0` §9): seen anywhere earlier it would not be unseen.
                    roles, scopes = [TRANSFER_ROLE], ["monthly_capability"]
                independence = "h0_required" if it.get("independence", "h0") == "h0" else "guided_allowed"
                items_out.append(section("item", [
                    ("ref", f"{iid}@v{ver}"), ("prompt", esc(prompt, multiline=True)),
                    ("target_objectives", pin(oid)), ("target_skills", pin(sid)),
                    ("required_skills", [pin(r) for r in sorted(declared)]),
                    ("evidence_type", evidence_type),
                    ("expected_answer_or_rubric_ref", f"rubric.{base}.{it['slug']}@v{ver}" if is_rubric
                     else f"codetest.{base}.{it['slug']}@v{ver}" if is_code else f"answerkey.{base}.{it['slug']}@v{ver}"),
                    ("evaluator_required_status", "provisional_allowed" if is_rubric else "verified"),
                    ("evaluator_deterministic_required", not is_rubric),
                    ("evaluator_policy_version", "OREX-v0/rubric" if is_rubric else "CDEX-v0/code_tests" if is_code else "OREX-v0/answer_key"),
                    ("deterministic_verification", not is_rubric),
                    # Reading code is done by reading; writing code is done at the learner's computer, where running
                    # it and reading documentation are part of the work (QAB-v0 §21). An AI never writes it for them.
                    ("allowed_tools", ["terminal", "documentation"] if at_computer else []),
                    ("prohibited_solution_sources", ["external_ai"] if at_computer else ["terminal", "compiler", "debugger", "external_ai"]),
                    ("independence_mode", independence), ("difficulty_class", difficulty),
                    ("lifecycle_status", status), ("content_origin", "ai_generated"),
                    ("declared_use_ceiling", "low_stakes_assessment" if is_rubric else "standard_mastery_eligible"),
                    ("scope_eligibility", scopes),
                    ("variant_family_id", family), ("forbidden_not_yet_concepts", sorted(set(introduced) - reachable)),
                    ("expected_active_minutes", it.get("minutes", notation["minutes_code" if at_computer else "minutes"][difficulty])),
                    ("blueprint_roles", sorted(set(roles))),
                ] + ([("transfer_profile", transfer)] if transfer is not None else [])
                  # A transfer item without a named context is already a reported failure (`transfer_problems`); it is
                  # written without one, never a crash of the whole build (found by the 15G builder mutation run).
                  + ([("context_family_id", f"context.{it['context_family']}")] if transfer is not None and it.get("context_family") else [])))
                # 15H: a revised choice item keeps the wrong-option keys mapped for the version it replaces — with the
                # verdict that review gave them only while its options are unchanged.
                keyed = it.get("misconception_options") or (
                    (supplement.get("earlier_misconception_options") or {}).get(iid) if ver > 1 else None)
                if keyed:
                    answer_misconceptions_out.extend(answer_misconception_sections(
                        iid, base, ns, it, keyed, misconception_slugs, report, version=ver,
                        carried=supplement.get("earlier_mapping_review") if ver > 1 and it.get("_options_unchanged") else None))
                if is_rubric:
                    rubrics_out.append(section("rubric", [("logical_id", f"rubric.{base}.{it['slug']}"), ("version", ver), ("item", f"{iid}@v{ver}")]))
                    for c in rubric:
                        rubrics_out.append(section("rubric_criterion", [
                            ("rubric", f"rubric.{base}.{it['slug']}@v{ver}"), ("id", c["id"]), ("objective", pin(oid)), ("statement", esc(c["statement"])),
                        ]))
                elif is_code:
                    suites_out.append(section("code_test_suite", [("logical_id", f"codetest.{base}.{it['slug']}"), ("version", ver),
                                                                  ("item", f"{iid}@v{ver}")]))
                    for t in it["suite"]:
                        suites_out.append(section("code_test", [("suite", f"codetest.{base}.{it['slug']}@v{ver}"), ("id", t["id"]),
                                                                ("objective", pin(oid))]))
                else:
                    keys_out.append(section("answer_key", [
                        ("logical_id", f"answerkey.{base}.{it['slug']}"), ("version", ver), ("item", f"{iid}@v{ver}"),
                        ("objective", pin(oid)), ("case_sensitive", False),
                    ]))
                    accepted = [str(it["answer"])] + [str(a) for a in it.get("also_accept", [])]
                    for a in accepted:
                        keys_out.append(section("accepted_answer", [("key", f"answerkey.{base}.{it['slug']}@v{ver}"), ("text", esc(a))]))
                if ver == 1:
                    item_refs[oid].append(iid)

        # -------------------------------------------------------------- tasks
        objectives = [o["id"] for o in doc["objectives"]]
        # 15G: a supplement's task presents the published items and the added ones, in the same planning rule; an item
        # reserved for a transfer slot is never in a task.
        # 15H (`D-121`): three pools, each pinned by version — the directory's own items (stage 0), what earlier
        # supplements published beside them (stage 1, what ships today), and what this supplement makes of them (stage 2:
        # its added items, and each revised item at its new version in place of the one it retires).
        def pool(o, stage: int) -> list[tuple[str, dict]]:
            act = o["id"].rsplit(".", 1)[1]
            entries = [(f"item.{ns}.{act}.{it['slug']}", it) for it in o["items"]]
            if stage >= 1:
                entries += [(f"item.{ns}.{act}.{it['slug']}", it) for it in earlier_added.get(o["id"], [])]
            if stage >= 2:
                entries += [(f"item.{ns}.{act}.{it['slug']}", it) for it in (supplement or {}).get("items", {}).get(o["id"], [])]
            pinned = []
            for i, it in entries:
                if i in transfer_only:
                    continue
                version = item_version.get(i, 1) if stage >= 2 else 1
                pinned.append((f"{i}@v{version}", stage_items[i] if version > 1 else it))
            return pinned

        def items_where(pred, stage: int):
            # An item shown inside the lesson belongs to the lesson only: offered again later it would measure recall
            # of the lesson, not the capability (independent review, 15A).
            return [ref for o in doc["objectives"] for ref, it in pool(o, stage)
                    if pred(it) and (it.get("in_lesson") is True) == (pred is lesson_items)]

        def lesson_items(it):
            return bool(it.get("in_lesson"))
        def exps(forms):
            return [e for o in objectives for e in explanation_refs[o] if any(e.endswith("." + f) or ("." + f + "_") in e for f in forms)]
        def make_plans(w: int) -> dict:
            return {
                "teach": dict(purpose="teach", activity="content_explanation", serves=["new_learning"],
                              explanations=exps(["canonical", "worked_example"]),
                              items=items_where(lesson_items, w)),
                "practice": dict(purpose="practice", activity=doc["activity"], serves=["continue_learning"], explanations=[],
                                 items=items_where(lambda it: it["difficulty"] in ("basic", "authentic_application") and "rubric" not in it, w)
                                 + items_where(lambda it: "rubric" in it, w)),
                "check": dict(purpose="assess", activity=doc["activity"], serves=["verification_due"], explanations=[],
                              items=items_where(lambda it: it["difficulty"] != "basic" and "rubric" not in it, w), atomic=True),
                "review": dict(purpose="retain", activity=doc["activity"], serves=["retention_review_due"], explanations=[],
                               items=items_where(lambda it: "rubric" not in it, w), atomic=True),
                "repair": dict(purpose="remediate", activity=doc["activity"], serves=["remediation_required", "weakness_detected"],
                               explanations=exps(["prerequisite_refresh", "state_trace", "plain_reteach", "different_example", "contrast"]),
                               items=items_where(lambda it: "rubric" not in it, w)),
            }

        def required_of(plan: dict, stage: int) -> list[str]:
            # A task needs what its items need, and what its lesson text needs when the graph does not already say so
            # (3B §10: a task's own requirement is hard even where the graph is silent). 15H: a lesson's newly declared
            # need (`teach_requires`) is the same kind of requirement as `lesson_requires`.
            dicts = {ref: it for o in doc["objectives"] for ref, it in pool(o, stage)}
            return sorted({r for ref in plan["items"] for r in dicts[ref].get("required_skills", [])}
                          | set(doc.get("lesson_requires", []))
                          | (set(teach_requires.get(sid, [])) if stage >= 2 else set()))

        plans = make_plans(2)
        own_plans, published_plans = (make_plans(0), make_plans(1)) if supplement is not None else (plans, plans)
        for kind, plan in plans.items():
            spec = doc["tasks"][kind]
            tid = f"task.{ns}.{kind}"
            required = required_of(plan, 2)
            task_version = 1
            if supplement is not None:
                # A supplement publishes a task again only when what it presents, or what it needs, changed: the next
                # version, never an overwrite. (An earlier supplement may already have published the second.)
                if plan["items"] == published_plans[kind]["items"] and required == required_of(published_plans[kind], 1):
                    continue
                task_version = (1 if published_plans[kind]["items"] == own_plans[kind]["items"] else 2) + 1
            status = "validated" if all(item_status[ref.rsplit("@", 1)[0]] == "validated" for ref in plan["items"]) else "candidate"
            tasks_out.append(section("task", [
                ("logical_id", tid), ("version", task_version), ("title", esc(spec["title"])), ("primary_skill", pin(sid)),
                ("target_objectives", [pin(o) for o in objectives]), ("purpose", plan["purpose"]),
                ("activity_kind", plan["activity"]), ("serves", plan["serves"]), ("cost_minutes", spec["minutes"]),
                ("required_skills", [pin(r) for r in required]),
                ("explanations", [pin(e) for e in plan["explanations"]]), ("items", plan["items"]),
                ("lifecycle_status", status), ("content_origin", "ai_generated"),
                ("atomic_evidence_boundary", bool(plan.get("atomic"))),
            ]))
            report["tasks"].append({"task": tid, "status": status, "items": len(plan["items"]), "explanations": len(plan["explanations"]),
                                    **({"version": task_version} if supplement is not None and (earlier_added or revisions or teach_requires) else {})})
        if supplement is not None:
            # 15H: what the Skill's lesson and its items need after this supplement, for the edges it adds.
            report.setdefault("skill_requirements", {})[sid] = {
                "teach": required_of(plans["teach"], 2),
                "items": [sorted(it.get("required_skills", [])) for o in doc["objectives"] for ref, it in pool(o, 2) if not it.get("in_lesson")],
            }

    if supplement is not None:
        # The caller writes one supplement from every content directory's blocks, in one order.
        report["blocks"] = {"resources": resources, "validations": validations, "items": items_out, "keys": keys_out,
                            "answer_misconceptions": answer_misconceptions_out, "rubrics": rubrics_out,
                            "suites": suites_out, "tasks": tasks_out}
    for block in (misconceptions_out, resources, validations, explanations_out, items_out, keys_out, rubrics_out, suites_out, tasks_out):
        out.extend(block)

    text = "\n\n".join(out) + "\n"
    report["suite_files"] = suite_files
    report["summary"] = {
        "skills": len(skill_ids), "objectives": len(objective_parent), "topics": len(topics),
        "items": len(report["items"]), "validated_items": sum(1 for i in report["items"] if i["status"] == "validated"),
        "explanations": report["explanations"], "misconceptions": len(misconceptions_out), "tasks": len(tasks_out),
        "failures": len(report["failures"]),
    }
    return text, report


SUPPLEMENT_BLOCKS = ["resources", "validations", "items", "keys", "answer_misconceptions", "rubrics", "suites", "tasks"]


def build_supplement(content_dir: Path) -> tuple[str, dict]:
    """15G (`D-120`): a package that adds to Objectives already published — more items, new task versions, transfer
    items and wrong-option misconception keys — and changes nothing that was published. Each added item is checked by the
    content directory it belongs to, so its notation, lexicon, runner and prelude are the ones its lesson was written in."""
    pkg = load(content_dir / "package.yaml")
    review = load(content_dir / "independent_review.yaml") or {}
    reviewed = review.get("items", {}) or {}
    mapping_review = review.get("answer_misconceptions", {}) or {}
    # 15H (`D-121`): a supplement may build on an earlier one — its items, review and mappings count as published — and
    # may revise published items, declare a lesson's need, extend the reading notation, record fixed-output exemptions and
    # add edges to the graph. Each of those is its own file, absent from 15G, which therefore builds exactly as before.
    earlier: list[dict] = []
    for rel in pkg.get("earlier_supplements", []):
        e_review = load(ROOT / rel / "independent_review.yaml") or {}
        earlier.append({"dir": ROOT / rel, "review": e_review.get("items", {}) or {},
                        "mapping_review": e_review.get("answer_misconceptions", {}) or {}})
    graph = load(content_dir / "graph.yaml") if (content_dir / "graph.yaml").exists() else {}
    overlay = load(content_dir / "notation_overlay.yaml") if (content_dir / "notation_overlay.yaml").exists() else {}
    fixed_output = load(content_dir / "fixed_output.yaml") if (content_dir / "fixed_output.yaml").exists() else {}
    teach_requires = {s: spec["requires"] for s, spec in (graph.get("teach_requires") or {}).items()}
    out = ["\n".join([
        "curriculum_package/1",
        "# Generated by tools/build_curriculum_package.py from " + content_dir.relative_to(ROOT).as_posix() + " — do not edit by hand.",
        f"version={pkg['version']}",
        f"source_refs={pkg['source_refs']}",
        f"provenance={pkg['provenance']}",
    ])]
    blocks: dict[str, list[str]] = {b: [] for b in SUPPLEMENT_BLOCKS}
    report: dict = {"package_version": pkg["version"], "items": [], "tasks": [], "answer_misconceptions": [], "failures": [],
                    "suite_files": {}, "by_source": {}}
    requirements: dict[str, dict] = {}
    for rel in pkg["supplements"]:
        source = ROOT / rel
        added: dict[str, list[dict]] = {}
        revisions: dict[str, list[dict]] = {}
        options: dict[str, dict] = {}
        for path in sorted((content_dir / "items" / source.name).glob("*.yaml")):
            doc = load(path)
            for o in doc.get("objectives", []):
                added.setdefault(o["id"], []).extend(o.get("items", []))
                revisions.setdefault(o["id"], []).extend(o.get("revisions", []))
            options.update(doc.get("misconception_options") or {})
        earlier_items: dict[str, list[dict]] = {}
        earlier_options: dict[str, dict] = {}
        for e in earlier:
            for path in sorted((e["dir"] / "items" / source.name).glob("*.yaml")):
                doc = load(path)
                for o in doc.get("objectives", []):
                    earlier_items.setdefault(o["id"], []).extend(o.get("items", []))
                    for it in o.get("items", []):
                        if it.get("misconception_options"):
                            earlier_options[f"item.{o['id'].split('.', 1)[1]}.{it['slug']}"] = it["misconception_options"]
                earlier_options.update(doc.get("misconception_options") or {})
        _, sub = build(source, supplement={
            "items": added, "review": reviewed, "mapping_review": mapping_review, "misconception_options": options,
            "step": pkg["step"], "validated_at": pkg["validated_at_instant"],
            **({"earlier_items": earlier_items, "earlier_review": {k: v for e in earlier for k, v in e["review"].items()},
                "earlier_misconception_options": earlier_options,
                "earlier_mapping_review": {k: v for e in earlier for k, v in e["mapping_review"].items()},
                "revisions": revisions, "teach_requires": teach_requires, "notation_overlay": overlay,
                "fixed_output": fixed_output, "constant_check": bool(pkg.get("constant_check"))} if earlier else {}),
        })
        for b in SUPPLEMENT_BLOCKS:
            blocks[b].extend(sub["blocks"][b])
        report["items"].extend(sub["items"])
        report["tasks"].extend(sub["tasks"])
        report["answer_misconceptions"].extend(sub.get("answer_misconceptions", []))
        report["failures"].extend(sub["failures"])
        report["suite_files"].update(sub["suite_files"])
        report["by_source"][source.name] = {"added_items": sum(len(v) for v in added.values()), "tasks": len(sub["tasks"]),
                                            **({"revised_items": sum(len(v) for v in revisions.values())} if earlier else {})}
        requirements.update(sub.get("skill_requirements", {}))
    if graph:
        edge_sections, edge_report = supplement_edges(pkg, graph, review, requirements)
        out.extend(edge_sections)
        report["edges"] = edge_report["edges"]
        report["failures"].extend(edge_report["failures"])
    for b in SUPPLEMENT_BLOCKS:
        out.extend(blocks[b])
    text = "\n\n".join(out) + "\n"
    report["summary"] = {
        "items": len(report["items"]), "validated_items": sum(1 for i in report["items"] if i["status"] == "validated"),
        "transfer_items": sum(1 for b in blocks["items"] if "\ntransfer_profile=" in b),
        "task_versions": len(blocks["tasks"]), "answer_misconceptions": len(blocks["answer_misconceptions"]),
        "failures": len(report["failures"]),
        **({"revised_items": sum(1 for i in report["items"] if "revision" in i),
            "retired_versions": sum(1 for b in blocks["validations"] if "\nstatus=retired\n" in b),
            "edges": len(report.get("edges", []))} if earlier else {}),
    }
    return text, report


def published_graph() -> tuple[list[str], set[tuple[str, str]], dict[str, set[str]]]:
    """15H (`D-121`): the Skills and edges the shipped packages published, oldest first — exactly the rule `build` follows:
    a package publishes a 6C edge into one of its own Skills from a Skill it already knows."""
    edges6c = load(DECOMPOSITION / "prerequisite_edges.yaml")
    skills: list[str] = []
    published: set[tuple[str, str]] = set()
    hard: dict[str, set[str]] = {}
    for name in ["15a_computing_fundamentals", "15b_python_foundations", "15c_c_foundations", "15d_memory_foundations",
                 "15e_linux_git_shell", "15f_english_a1_a2"]:
        own = [s["id"] for s in load(ROOT / "curriculum/content" / name / "package.yaml")["skills"]]
        known = set(skills) | set(own)
        for e in edges6c:
            if e["target_skill_id"] in own and e["prerequisite_skill_id"] in known:
                published.add((e["prerequisite_skill_id"], e["target_skill_id"]))
                if e["edge_kind"] == "hard":
                    hard.setdefault(e["target_skill_id"], set()).add(e["prerequisite_skill_id"])
        skills += own
    return skills, published, hard


def closure_of(skill: str, graph: dict[str, set[str]]) -> set[str]:
    seen, todo = set(), [skill]
    while todo:
        for p in graph.get(todo.pop(), ()):
            if p not in seen:
                seen.add(p)
                todo.append(p)
    return seen


EDGE_RULES = {
    # The Skill's lesson needs P and so does every item outside it: it cannot be taught or fairly measured without P.
    "lesson_and_every_item": "hard",
    # The lesson needs P (its task already requires it, a hard requirement at task level), some items do not.
    "lesson": "soft",
    # The lesson reads structures P teaches and glosses them where it shows them: P helps, nothing is unfair without it.
    "reads_glossed": "soft",
}


def supplement_edges(pkg: dict, graph: dict, review: dict, requirements: dict[str, dict]) -> tuple[list[str], dict]:
    """15H (`D-121`): edges a supplement adds to the published graph, each derived from what the content needs.

    `requirements[skill]` is what the Skill's teach task requires and what each item outside its lesson declares, after
    this supplement. An edge is refused unless its rule holds; and every need a lesson declares that the graph does not
    already imply must be an edge (or be one already), so after the supplement the graph says what the lessons need. The
    hard graph, and hard edges together with every lesson's requirement, stay acyclic. Every edge and every newly declared
    lesson need carries a pass verdict from the independent review."""
    skills, published, hard = published_graph()
    edge_review = review.get("edges", {}) or {}
    teach_review = review.get("teach_requires", {}) or {}
    sections, rows, problems = [], [], []
    new_hard = {k: set(v) for k, v in hard.items()}
    for e in graph.get("edges", []):
        if e["kind"] == "hard":
            new_hard.setdefault(e["target"], set()).add(e["prerequisite"])
    declared_pairs = set()
    for e in graph.get("edges", []):
        p, t, kind, rule = e["prerequisite"], e["target"], e["kind"], e["rule"]
        key = f"{p}->{t}"
        declared_pairs.add((p, t))
        why = []
        req = requirements.get(t)
        if p not in skills or t not in skills or p == t:
            why.append("both ends are published Skills, and different")
        if (p, t) in published:
            why.append("the edge is already published")
        # Redundant against the graph after this supplement, not only the published one (independent review, 15H): an
        # edge the other hard edges already imply says nothing new.
        others = {k: (v - {p} if k == t and kind == "hard" else v) for k, v in new_hard.items()}
        if p in closure_of(t, others):
            why.append("the hard graph after this supplement already implies it")
        if EDGE_RULES.get(rule) != kind:
            why.append(f"rule {rule!r} gives a {EDGE_RULES.get(rule)} edge, not {kind}")
        if req is None:
            why.append("no requirements were computed for the target")
        else:
            in_lesson = p in req["teach"]
            every = bool(req["items"]) and all(p in declared for declared in req["items"])
            if rule == "lesson_and_every_item" and not (in_lesson and every):
                why.append(f"the lesson requires it: {in_lesson}; every item outside the lesson declares it: {every}")
            if rule == "lesson" and not (in_lesson and not every):
                why.append(f"a lesson edge needs the lesson's requirement and an item without it ({in_lesson}, {every})")
            if rule == "reads_glossed" and in_lesson:
                why.append("the lesson requires it: the edge is lesson or lesson_and_every_item, not reads_glossed")
        if edge_review.get(key, {}).get("verdict") != "pass":
            why.append("no pass verdict from the independent review")
        rows.append({"edge": key, "kind": kind, "rule": rule, "reason_kind": e["reason_kind"], "result": "PASS" if not why else "FAIL"})
        if why:
            problems.append({"item": f"edge {key}", "problems": why})
        sections.append(section("prerequisite_edge", [
            ("prerequisite", pin(p)), ("target", pin(t)), ("edge_version", 1), ("edge_kind", kind),
            ("reason_kind", e["reason_kind"]), ("strictness_profile", "default_prg_v0"), ("lifecycle_status", "published"),
            ("provenance", pkg["provenance"]),
        ]))
    for skill, spec in (graph.get("teach_requires") or {}).items():
        if teach_review.get(skill, {}).get("verdict") != "pass":
            problems.append({"item": f"teach requirement of {skill}", "problems": ["no pass verdict from the independent review"]})
    # Every lesson need is in the graph after this supplement.
    for t, req in sorted(requirements.items()):
        for p in req["teach"]:
            if p not in closure_of(t, new_hard) and (p, t) not in published and (p, t) not in declared_pairs:
                problems.append({"item": f"lesson need {p}->{t}", "problems": ["the lesson requires it and the graph does not say so"]})
    # Acyclic: the hard graph, and the hard graph with every lesson's requirement.
    for name, g in (("hard graph", new_hard),
                    ("hard graph with lesson requirements", {t: new_hard.get(t, set()) | set(r["teach"]) for t, r in requirements.items()})):
        cyclic = sorted(s for s in skills if s in closure_of(s, g))
        if cyclic:
            problems.append({"item": f"cycle in the {name}", "problems": cyclic[:6]})
    return sections, {"edges": rows, "failures": problems}


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("content_dir")
    ap.add_argument("--out")
    ap.add_argument("--report")
    ap.add_argument("--strict", action="store_true", help="fail if any item is not validated")
    ap.add_argument("--partial", action="store_true", help="authoring aid: build only the Skills that have content")
    args = ap.parse_args()
    if args.partial and args.out:
        raise SystemExit("a partial build is never written as the shipped package")
    content_dir = (ROOT / args.content_dir).resolve()
    if (load(content_dir / "package.yaml") or {}).get("supplements"):
        text, report = build_supplement(content_dir)
    else:
        text, report = build(content_dir, partial=args.partial)
    suite_files = report.pop("suite_files", {})
    if args.out:
        Path(ROOT / args.out).parent.mkdir(parents=True, exist_ok=True)
        with open(ROOT / args.out, "w", encoding="utf-8", newline="\n") as f:
            f.write(text)
        # The suites a learner runs on their own computer (14D) are written beside the content that declares them.
        for iid, body in sorted(suite_files.items()):
            target = content_dir / "suites" / f"{iid}.json"
            target.parent.mkdir(parents=True, exist_ok=True)
            with open(target, "w", encoding="utf-8", newline="\n") as f:
                f.write(body)
    if args.report:
        Path(ROOT / args.report).parent.mkdir(parents=True, exist_ok=True)
        with open(ROOT / args.report, "w", encoding="utf-8", newline="\n") as f:
            yaml.safe_dump(report, f, allow_unicode=True, sort_keys=False, width=120)
    print(yaml.safe_dump(report["summary"], allow_unicode=True, sort_keys=False).strip())
    for failure in report["failures"]:
        print("FAIL", failure["item"], "::", "; ".join(failure["problems"]))
    if args.strict and report["failures"]:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
