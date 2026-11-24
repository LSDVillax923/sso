import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { mensajeDeError } from '../../dto';
import { sesionVencida } from '../../errores';
import { ProxyCompra } from '../../proxy/proxy-compra';
import { ProxyOferta } from '../../proxy/proxy-oferta';
import type { PanMisSubastas } from './pan-mis-subastas';

/** Controlador de PanMisSubastas: muestra las subastas ganadas e inicia la compra (CU5). */
@Injectable()
export class CtrlMisSubastas {

  pan?: PanMisSubastas;

  constructor(private proxyOferta: ProxyOferta, private proxyCompra: ProxyCompra, private router: Router) {}

  cargar(): void {
    this.proxyOferta.ganadoras().subscribe({
      next: ofertas => this.pan?.mostrarGanadas(ofertas),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        this.pan?.mostrarGanadas([]);
        if (this.pan) this.pan.mensaje = mensajeDeError(e, 'No se pudieron consultar sus subastas');
      }
    });
  }

  iniciarCompra(idOferta: number): void {
    this.proxyCompra.iniciarCompra(idOferta).subscribe({
      // 202: OFAC se verifica por cola; PanEstadoCompra explica que llegará un SMS
      next: r => {
        if (this.pan) this.pan.mensaje = r.mensaje;
        this.router.navigate(['/compra', r.numeroCompra]);
      },
      error: e => {
        if (sesionVencida(e, this.router)) return;
        if (this.pan) this.pan.mensaje = mensajeDeError(e, 'No se pudo iniciar la compra');
      }
    });
  }
}
