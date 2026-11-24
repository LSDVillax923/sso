// Copia el build de Angular a sso-web, que lo sirve en PC1 (:8443) junto con la API.
import fs from 'node:fs';
import path from 'node:path';

const origen = path.resolve('dist/sso-web-spa/browser');
const destino = path.resolve('../sso-web/src/main/resources/static');
if (!fs.existsSync(origen)) {
  console.error('No existe ' + origen + ': corra primero ng build');
  process.exit(1);
}
fs.rmSync(destino, { recursive: true, force: true });
fs.cpSync(origen, destino, { recursive: true });
console.log('SPA copiado a ' + destino + ': ' + fs.readdirSync(destino).join(', '));
