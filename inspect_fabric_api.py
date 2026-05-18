from pathlib import Path
import zipfile

root = Path(r'C:\Users\PC\.gradle\caches\modules-2\files-2.1\net.fabricmc.fabric-api\fabric-api\0.87.0+1.20.1')
jars = list(root.glob('**/fabric-api-0.87.0+1.20.1.jar'))
if not jars:
    raise SystemExit('jar not found')
jar = jars[0]
print('jar:', jar)
with zipfile.ZipFile(jar, 'r') as z:
    nested = [name for name in z.namelist() if name.endswith('.jar')]
    print('nested count', len(nested))
    terms = [
        'FabricDefaultAttributeRegistry',
        'DefaultAttributeRegistry',
        'FabricEntityTypeBuilder',
        'attribute',
        'entity',
        'registry/Registry',
        'command/argument',
        'screen/ScreenHandlerType',
    ]
    for n in nested:
        data = z.read(n)
        tmp = Path('tmp_nested.jar')
        tmp.write_bytes(data)
        with zipfile.ZipFile(tmp, 'r') as nz:
            names = [name for name in nz.namelist() if any(term in name for term in terms)]
            if names:
                print('NESTED', n)
                for name in names:
                    print(' ', name)
        tmp.unlink()
