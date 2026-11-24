import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { mensajeDeError } from '../../dto';
import { sesionVencida } from '../../errores';
import { ProxyCarrito } from '../../proxy/proxy-carrito';
import type { PanCarritoSubastas } from './pan-carrito-subastas';

/** Controlador de PanCarritoSubastas: lista y quita ítems, y abre PanDatosOferta con los bienes del carrito. */
@Injectable()
export class CtrlCarritoSubastas {

  pan?: PanCarritoSubastas;

  constructor(private proxyCarrito: ProxyCarrito, private router: Router) {}

  cargar(): void {
    this.proxyCarrito.listar().subscribe({
      next: items => this.pan?.mostrarItems(items),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        this.pan?.mostrarItems([]);
        if (this.pan) this.pan.aviso = mensajeDeError(e, 'No se pudo consultar el carrito');
      }
    });
  }

  quitar(idItem: number): void {
    this.proxyCarrito.quitar(idItem).subscribe({
      next: () => this.cargar(),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        if (this.pan) this.pan.aviso = mensajeDeError(e, 'No se pudo quitar el ítem');
      }
    });
  }

  irAOfertar(): void {
    const items = this.pan?.items ?? [];
    if (!items.length) return;
    // PanDatosOferta recibe los bienes del carrito en el estado de la navegación
    this.router.navigate(['/oferta'], { state: { items } });
  }
}
