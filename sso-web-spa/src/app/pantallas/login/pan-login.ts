import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CtrlLogin } from './ctrl-login';

/** CU1 · el cliente se autentica con su usuario del Active Directory. */
@Component({
  selector: 'sso-pan-login',
  standalone: true,
  imports: [FormsModule],
  providers: [CtrlLogin],
  template: `
    <section class="tarjeta angosta">
      <h1>Ingresar</h1>
      <p class="nota">Use el usuario y la clave con los que entra a las aplicaciones de la empresa.</p>
      <form (ngSubmit)="ctrlLogin.ingresar(usuario, clave)">
        <label>Usuario
          <input name="usuario" [(ngModel)]="usuario" autocomplete="username" autofocus>
        </label>
        <label>Clave
          <input name="clave" type="password" [(ngModel)]="clave" autocomplete="current-password">
        </label>
        @if (error) { <p class="error">{{ error }}</p> }
        <button type="submit" class="primario">Ingresar</button>
      </form>
    </section>
  `
})
export class PanLogin {

  usuario = '';
  clave = '';
  error = '';

  constructor(public ctrlLogin: CtrlLogin) {
    ctrlLogin.pan = this;
  }

  mostrarError(msg: string): void {
    this.error = msg;
  }
}
