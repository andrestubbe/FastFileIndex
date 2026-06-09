import os
import zipfile
from pathlib import Path
repo = os.environ.get('TEMP')
jar = Path(repo) / 'FastFileIndex-jitpack-m2' / 'com' / 'github' / 'andrestubbe' / 'FastFileIndex' / '0.1.0' / 'FastFileIndex-0.1.0.jar'
print('TEMP repo jar:', jar)
print('exists:', jar.exists())
if jar.exists():
    with zipfile.ZipFile(jar) as z:
        names = z.namelist()
        print('has native/fastfileindex.dll:', 'native/fastfileindex.dll' in names)
        print('native entry count:', sum(1 for n in names if n.startswith('native/')))
        print('has fastcore classes:', any(n.startswith('fastcore/') for n in names))
        print('has fastfileindex classes:', any(n.startswith('fastfileindex/') for n in names))
