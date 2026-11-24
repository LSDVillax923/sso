import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ItemCarritoDTO } from '../../dto';
import { CtrlCarritoSubastas } from './ctrl-carrito-subastas';

/** CU2 y CU3 · el carrito de subastas del cliente; desde aquí se pasa a ofertar. */
@Component({
  selector: 'sso-pan-carrito-subastas',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, RouterLink],
  providers: [CtrlCarritoSubastas],
  template: `
    <section>
      <h1>Mi carrito de subastas</h1>
      @if (aviso) { <p class="error">{{ aviso }}</p> }
      @if (items.length) {
        <table>
          <thead><tr><th>Bien</th><th>Valor mínimo</th><th>Cierra</th><th></th></tr></thead>
          <tbody>
            @for (i of items; track i.idItem) {
              <tr>
                <td>{{ i.nombreBien }}</td>
                <td>{{ i.valorMinimo | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
                <td>{{ i.fechaCierre | date:'d MMM y, h:mm a' }}</td>
                <td><button class="enlace" (click)="ctrlCarritoSubastas.quitar(i.idItem)">Quitar</button></td>
              </tr>
            }
          </tbody>
        </table>
        <div class="acciones">
          <a routerLink="/catalogo">Seguir buscando</a>
          <button class="primario" (click)="ctrlCarritoSubastas.irAOfertar()">Ofertar</button>
        </div>
      } @else {
        <p class="nota">Su carrito está vacío. <a routerLink="/catalogo">Busque bienes en subasta</a>.</p>
      }
    </section>
  `
})
export class PanCarritoSubastas implements OnInit {

  items: ItemCarritoDTO[] = [];
  aviso = '';

  constructor(public ctrlCarritoSubastas: CtrlCarritoSubastas) {
    ctrlCarritoSubastas.pan = this;
  }

  ngOnInit(): void {
    this.ctrlCarritoSubastas.cargar();
  }

  mostrarItems(items: ItemCarritoDTO[]): void {
    this.aviso = '';
    this.items = items;
  }
}
