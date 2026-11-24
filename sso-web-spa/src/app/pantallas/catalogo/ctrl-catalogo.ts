import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { mensajeDeError } from '../../dto';
import { sesionVencida } from '../../errores';
import { ProxyCarrito } from '../../proxy/proxy-carrito';
import { ProxyCatalogo } from '../../proxy/proxy-catalogo';
import type { PanCatalogo } from './pan-catalogo';

/** Controlador de PanCatalogo: busca los bienes en subasta y los agrega al carrito. */
@Injectable()
export class CtrlCatalogo {

  pan?: PanCatalogo;

  constructor(private proxyCatalogo: ProxyCatalogo, private proxyCarrito: ProxyCarrito, private router: Router) {}

  buscar(filtro: string): void {
    this.proxyCatalogo.buscarBienes(filtro).subscribe({
      next: bienes => this.pan?.mostrarBienes(bienes),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        this.pan?.mostrarBienes([]);
        if (this.pan) this.pan.aviso = mensajeDeError(e, 'No se pudo consultar el catálogo');
      }
    });
  }

  agregarAlCarrito(idSubasta: number): void {
    this.proxyCarrito.agregar(idSubasta).subscribe({
      next: () => this.router.navigate(['/carrito']),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        if (this.pan) this.pan.aviso = mensajeDeError(e, 'No se pudo agregar el bien al carrito');
      }
    });
  }
}
