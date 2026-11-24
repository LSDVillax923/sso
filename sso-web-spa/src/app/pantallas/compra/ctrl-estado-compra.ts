import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { mensajeDeError } from '../../dto';
import { sesionVencida } from '../../errores';
import { ProxyCompra } from '../../proxy/proxy-compra';
import type { PanEstadoCompra } from './pan-estado-compra';

/** Controlador de PanEstadoCompra: consulta en qué va la compra (OFAC, pago, distribución). */
@Injectable()
export class CtrlEstadoCompra {

  pan?: PanEstadoCompra;

  constructor(private proxyCompra: ProxyCompra, private router: Router) {}

  consultar(numeroCompra: number): void {
    this.proxyCompra.consultar(numeroCompra).subscribe({
      next: compra => this.pan?.mostrarEstado(compra),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        if (this.pan) this.pan.aviso = mensajeDeError(e, 'No se encontró la compra ' + numeroCompra);
      }
    });
  }
}
