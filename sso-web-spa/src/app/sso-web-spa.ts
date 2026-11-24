import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CLAVE_NOMBRE, haySesion } from './proxy/proxy-autenticacion';
import { CtrlLogin } from './pantallas/login/ctrl-login';

/** Componente raíz del SSOWebSPA: barra de navegación y el lugar donde se abre cada pantalla. */
@Component({
  selector: 'sso-web-spa',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  providers: [CtrlLogin],
  template: `
    <header>
      <span class="marca">SSO · Subastas en línea</span>
      @if (conSesion()) {
        <nav>
          <a routerLink="/catalogo" routerLinkActive="activo">Catálogo</a>
          <a routerLink="/carrito" routerLinkActive="activo">Carrito</a>
          <a routerLink="/mis-subastas" routerLinkActive="activo">Mis subastas</a>
        </nav>
        <span class="usuario">{{ nombre() }}</span>
        <button class="enlace" (click)="ctrlLogin.cerrarSesion()">Salir</button>
      }
    </header>
    <main><router-outlet></router-outlet></main>
  `
})
export class SSOWebSPA {

  constructor(public ctrlLogin: CtrlLogin) {}

  conSesion(): boolean {
    return haySesion();
  }

  nombre(): string {
    return sessionStorage.getItem(CLAVE_NOMBRE) ?? '';
  }
}
