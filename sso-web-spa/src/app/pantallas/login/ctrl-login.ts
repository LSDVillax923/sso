import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { mensajeDeError } from '../../dto';
import { ProxyAutenticacion } from '../../proxy/proxy-autenticacion';
import type { PanLogin } from './pan-login';

/** Controlador de PanLogin: el usuario y la clave se validan en el AD a través del SSO. */
@Injectable()
export class CtrlLogin {

  pan?: PanLogin;

  constructor(private proxyAutenticacion: ProxyAutenticacion, private router: Router) {}

  ingresar(usuario: string, clave: string): void {
    if (!usuario?.trim() || !clave) {
      this.pan?.mostrarError('Escriba su usuario del directorio y su clave');
      return;
    }
    this.proxyAutenticacion.login({ usuario: usuario.trim(), clave }).subscribe({
      next: () => this.router.navigate(['/catalogo']),
      error: e => this.pan?.mostrarError(mensajeDeError(e, 'Usuario o clave incorrectos'))
    });
  }

  cerrarSesion(): void {
    this.proxyAutenticacion.logout();
    this.router.navigate(['/login']);
  }
}
