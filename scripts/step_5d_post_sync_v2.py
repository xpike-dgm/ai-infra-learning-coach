from pathlib import Path

src = Path('scripts/step_5d_post_sync.py').read_text(encoding='utf-8')
needle = "fbbtxt=read(fbb)\n"
patch = '''replace_once(fbb,\n''' + "'''skill.python.for_iteration\n  --soft/supporting-->\nskill.dsa.sequence_traversal_linear_search'''" + ''',\n''' + "'''skill.python.for_iteration\n  --soft/conceptual_dependency-->\nskill.dsa.sequence_traversal_linear_search'''" + ''')\n\n'''
if needle not in src:
    raise SystemExit('v2 injection marker missing')
src = src.replace(needle, patch + needle, 1)
exec(compile(src, 'step_5d_post_sync_patched.py', 'exec'))
