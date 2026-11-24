import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BienDTO } from '../../dto';
import { CtrlCatalogo } from './ctrl-catalogo';

/** CU2 · el cliente busca bienes en subasta y arma su carrito. */
@Component({
  selector: 'sso-pan-catalogo',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe],
  providers: [CtrlCatalogo],
  template: `
    <section>
      <h1>Bienes en subasta</h1>
      <form class="buscador" (ngSubmit)="ctrlCatalogo.buscar(filtro)">
        <input name="filtro" [(ngModel)]="filtro" placeholder="Nombre o descripción del bien">
        <button type="submit" class="primario">Buscar</button>
      </form>
      @if (aviso) { <p class="error">{{ aviso }}</p> }
      <div class="rejilla">
        @for (b of bienes; track b.idSubasta) {
          <article class="tarjeta">
            <h2>{{ b.nombre }}</h2>
            <p>{{ b.descripcion }}</p>
            <dl>
              <dt>Valor mínimo</dt><dd>{{ b.valorMinimo | currency:'COP':'symbol-narrow':'1.0-0' }}</dd>
              <dt>Cierra</dt><dd>{{ b.fechaCierre | date:'d MMM y, h:mm a' }}</dd>
            </dl>
            <button class="primario" (click)="ctrlCatalogo.agregarAlCarrito(b.idSubasta)">Agregar al carrito</button>
          </article>
        } @empty {
          <p class="nota">No hay bienes en subasta con ese filtro.</p>
        }
      </div>
    </section>
  `
})
export class PanCatalogo implements OnInit {

  filtro = '';
  bienes: BienDTO[] = [];
  aviso = '';

  constructor(public ctrlCatalogo: CtrlCatalogo) {
    ctrlCatalogo.pan = this;
  }

  ngOnInit(): void {
    this.ctrlCatalogo.buscar('');
  }

  mostrarBienes(bienes: BienDTO[]): void {
    this.aviso = '';
    this.bienes = bienes;
  }
}
