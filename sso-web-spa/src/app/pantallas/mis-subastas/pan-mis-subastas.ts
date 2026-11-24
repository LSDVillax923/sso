import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { OfertaDTO } from '../../dto';
import { CtrlMisSubastas } from './ctrl-mis-subastas';

/** CU5 · el cliente entra con el enlace del correo y pulsa Iniciar compra. */
@Component({
  selector: 'sso-pan-mis-subastas',
  standalone: true,
  imports: [CurrencyPipe],
  providers: [CtrlMisSubastas],
  template: `
    <section>
      <h1>Mis subastas ganadas</h1>
      @if (mensaje) { <p class="aviso">{{ mensaje }}</p> }
      @if (ganadas.length) {
        <table>
          <thead><tr><th>Bien</th><th>Mi oferta</th><th></th></tr></thead>
          <tbody>
            @for (o of ganadas; track o.idOferta) {
              <tr>
                <td>{{ o.nombreBien }}</td>
                <td>{{ o.valor | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
                <td><button class="primario" (click)="ctrlMisSubastas.iniciarCompra(o.idOferta)">Iniciar compra</button></td>
              </tr>
            }
          </tbody>
        </table>
      } @else {
        <p class="nota">Todavía no ha ganado subastas. Cuando gane una le llega un correo con el enlace a esta pantalla.</p>
      }
    </section>
  `
})
export class PanMisSubastas implements OnInit {

  ganadas: OfertaDTO[] = [];
  mensaje = '';

  constructor(public ctrlMisSubastas: CtrlMisSubastas) {
    ctrlMisSubastas.pan = this;
  }

  ngOnInit(): void {
    this.ctrlMisSubastas.cargar();
  }

  mostrarGanadas(ofertas: OfertaDTO[]): void {
    this.ganadas = ofertas;
  }
}
