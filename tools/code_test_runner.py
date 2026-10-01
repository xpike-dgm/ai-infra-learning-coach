"""Course code-test runner (14D, `CDEX-v0 / D-108`) — runs on the learner's own computer.

The phone is not an IDE (`LEARNING_BEHAVIOR_RULES` §1): the learner writes and runs code on their computer, runs this
runner against the course's suite for the task, and pastes the report it prints back into the app. The app reads the
report strictly (`CodeTestReports.decode`) and decides what it proves; this runner decides nothing about the learner.

    python code_test_runner.py SUITE.json [--dir DIRECTORY]

Suite (`code_test_suite/1`, JSON, authored with the course content):

    {
      "format": "code_test_suite/1",
      "suite": "codetest.<namespace>.<slug>@v<N>",
      "item": "<item logical id>@v<N>",
      "build": {"command": ["gcc", "-std=c11", "main.c", "-o", "main"]},   # optional; omitted -> not_required
      "run": {"command": ["./main"]},
      "timeout_seconds": 5,                                               # authored, never defaulted
      "compare": "exact" | "trim_trailing_whitespace",
      "tests": [{"id": "adds_small", "stdin": "2 3\\n", "args": [], "expected_stdout": "5\\n", "expected_exit_code": 0}]
    }

`{python}` in a command is replaced by the Python running this runner. Standard library only; commands run without a
shell. A command that cannot be started is an environment error — it says nothing about the learner's code.
"""

from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

SUITE_FORMAT = "code_test_suite/1"
REPORT_FORMAT = "code_test_report/1"
SUITE_KEYS = {"format", "suite", "item", "build", "run", "timeout_seconds", "compare", "tests"}
TEST_KEYS = {"id", "stdin", "args", "expected_stdout", "expected_exit_code"}
COMPARES = {"exact", "trim_trailing_whitespace"}


class SuiteError(ValueError):
    pass


def load_suite(path: Path) -> dict:
    try:
        suite = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        raise SuiteError(f"cannot read suite: {error}") from error
    if not isinstance(suite, dict) or suite.get("format") != SUITE_FORMAT:
        raise SuiteError(f"not a {SUITE_FORMAT} document")
    unknown = set(suite) - SUITE_KEYS
    if unknown:
        raise SuiteError(f"unknown keys {sorted(unknown)}")
    for key in ("suite", "item"):
        value = suite.get(key)
        if not isinstance(value, str) or "@v" not in value:
            raise SuiteError(f"'{key}' is not a pinned reference (id@vN)")
    if not isinstance(suite.get("timeout_seconds"), (int, float)) or suite["timeout_seconds"] <= 0:
        raise SuiteError("'timeout_seconds' must be authored and positive")
    if suite.get("compare") not in COMPARES:
        raise SuiteError(f"'compare' must be one of {sorted(COMPARES)}")
    for key in ("build", "run"):
        if key in suite and not (isinstance(suite[key], dict) and isinstance(suite[key].get("command"), list) and suite[key]["command"]):
            raise SuiteError(f"'{key}' needs a non-empty command list")
    if "run" not in suite:
        raise SuiteError("'run' is required")
    tests = suite.get("tests")
    if not isinstance(tests, list) or not tests:
        raise SuiteError("a suite with no test measures nothing")
    seen = set()
    for test in tests:
        if not isinstance(test, dict) or set(test) - TEST_KEYS or "id" not in test or "expected_stdout" not in test:
            raise SuiteError(f"malformed test {test!r}")
        if test["id"] in seen:
            raise SuiteError(f"test '{test['id']}' is declared twice")
        seen.add(test["id"])
    return suite


def command(parts: list, extra: list | None = None) -> list:
    return [sys.executable if part == "{python}" else str(part) for part in parts] + [str(a) for a in (extra or [])]


def normalise(text: str, compare: str) -> str:
    text = text.replace("\r\n", "\n")
    if compare == "trim_trailing_whitespace":
        text = "\n".join(line.rstrip() for line in text.split("\n")).rstrip("\n")
    return text


def run_build(suite: dict, directory: Path) -> str:
    if "build" not in suite:
        return "not_required"
    try:
        done = subprocess.run(command(suite["build"]["command"]), cwd=directory, capture_output=True, timeout=suite["timeout_seconds"])
    except (OSError, subprocess.TimeoutExpired):
        return "environment_error"
    return "ok" if done.returncode == 0 else "failed"


def run_test(suite: dict, test: dict, directory: Path) -> str:
    try:
        done = subprocess.run(
            command(suite["run"]["command"], test.get("args")),
            cwd=directory,
            input=test.get("stdin", "").encode("utf-8"),
            capture_output=True,
            timeout=suite["timeout_seconds"],
        )
    except subprocess.TimeoutExpired:
        return "timed_out"
    except OSError:
        return "error"
    output = done.stdout.decode("utf-8", errors="replace")
    if normalise(output, suite["compare"]) != normalise(test["expected_stdout"], suite["compare"]):
        return "failed"
    if "expected_exit_code" in test and done.returncode != test["expected_exit_code"]:
        return "failed"
    return "passed"


def report(suite: dict, directory: Path) -> str:
    build = run_build(suite, directory)
    lines = [REPORT_FORMAT, f"item: {suite['item']}", f"suite: {suite['suite']}", f"build: {build}"]
    for test in suite["tests"]:
        status = run_test(suite, test, directory) if build in ("ok", "not_required") else "not_run"
        lines.append(f"test: {test['id']} {status}")
    lines.append("end")
    return "\n".join(lines) + "\n"


def main(argv: list[str]) -> int:
    if not argv or argv[0] in ("-h", "--help"):
        print(__doc__)
        return 0 if argv else 2
    directory = Path.cwd()
    if "--dir" in argv:
        at = argv.index("--dir")
        directory = Path(argv[at + 1])
        argv = argv[:at] + argv[at + 2:]
    try:
        suite = load_suite(Path(argv[0]))
    except SuiteError as error:
        print(f"suite error: {error}", file=sys.stderr)
        return 2
    # Bytes, so the report is identical on every operating system (no newline translation).
    sys.stdout.flush()
    sys.stdout.buffer.write(report(suite, directory).encode("utf-8"))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
