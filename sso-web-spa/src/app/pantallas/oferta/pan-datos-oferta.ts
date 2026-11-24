import { CurrencyPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ItemCarritoDTO, ItemOfertaDTO } from '../../dto';
import { CtrlDatosOferta } from './ctrl-datos-oferta';

/** CU3 · el cliente escribe el valor que ofrece por cada bien del carrito. */
@Component({
  selector: 'sso-pan-datos-oferta',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, RouterLink],
  providers: [CtrlDatosOferta],
  template: `
    <section>
      <h1>Datos de la oferta</h1>
      @if (items.length) {
        <form (ngSubmit)="ctrlDatosOferta.enviarOferta(items)">
          <table>
            <thead><tr><th>Bien</th><th>Valor mínimo</th><th>Mi oferta</th></tr></thead>
            <tbody>
              @for (i of items; track i.idSubasta; let n = $index) {
                <tr>
                  <td>{{ bienes[n].nombreBien }}</td>
                  <td>{{ bienes[n].valorMinimo | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
                  <td><input type="number" min="0" step="1000" [name]="'valor' + n" [(ngModel)]="i.valor"></td>
                </tr>
              }
            </tbody>
          </table>
          <p class="nota">Deje en 0 los bienes por los que no quiere ofertar.</p>
          @if (mensaje) { <p class="aviso">{{ mensaje }}</p> }
          <div class="acciones">
            <a routerLink="/carrito">Volver al carrito</a>
            <button type="submit" class="primario">Enviar oferta</button>
          </div>
        </form>
      } @else {
        <p class="nota">No hay bienes para ofertar. <a routerLink="/carrito">Vaya a su carrito</a>.</p>
      }
    </section>
  `
})
export class PanDatosOferta {

  items: ItemOfertaDTO[] = [];
  mensaje = '';
  /** Los bienes del carrito con los que se abrió la pantalla, para mostrar nombre y valor mínimo. */
  bienes: ItemCarritoDTO[] = [];

  constructor(public ctrlDatosOferta: CtrlDatosOferta) {
    ctrlDatosOferta.pan = this;
    this.bienes = (history.state?.items ?? []) as ItemCarritoDTO[];
    this.items = this.bienes.map(b => ({ idSubasta: b.idSubasta, valor: b.valorMinimo }));
  }

  mostrarMensaje(msg: string): void {
    this.mensaje = msg;
  }
}
