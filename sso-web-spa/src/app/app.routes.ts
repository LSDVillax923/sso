import { inject } from '@angular/core';
import { Router, Routes } from '@angular/router';
import { PanCarritoSubastas } from './pantallas/carrito/pan-carrito-subastas';
import { PanCatalogo } from './pantallas/catalogo/pan-catalogo';
import { PanEstadoCompra } from './pantallas/compra/pan-estado-compra';
import { PanLogin } from './pantallas/login/pan-login';
import { PanMisSubastas } from './pantallas/mis-subastas/pan-mis-subastas';
import { PanDatosOferta } from './pantallas/oferta/pan-datos-oferta';
import { haySesion } from './proxy/proxy-autenticacion';

const conSesion = () => haySesion() || inject(Router).parseUrl('/login');

// Mismas rutas que ControladorSPA de sso-web devuelve con index.html
export const rutas: Routes = [
  { path: 'login', component: PanLogin },
  { path: 'catalogo', component: PanCatalogo, canActivate: [conSesion] },
  { path: 'carrito', component: PanCarritoSubastas, canActivate: [conSesion] },
  { path: 'oferta', component: PanDatosOferta, canActivate: [conSesion] },
  { path: 'mis-subastas', component: PanMisSubastas, canActivate: [conSesion] },
  { path: 'compra/:numero', component: PanEstadoCompra, canActivate: [conSesion] },
  { path: '', pathMatch: 'full', redirectTo: 'catalogo' },
  { path: '**', redirectTo: 'catalogo' }
];
