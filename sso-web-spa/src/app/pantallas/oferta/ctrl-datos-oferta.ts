import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ItemOfertaDTO, RespuestaOfertaDTO, mensajeDeError } from '../../dto';
import { sesionVencida } from '../../errores';
import { ProxyOferta } from '../../proxy/proxy-oferta';
import type { PanDatosOferta } from './pan-datos-oferta';

/** Controlador de PanDatosOferta: manda en una sola petición las ofertas de uno o varios bienes. */
@Injectable()
export class CtrlDatosOferta {

  pan?: PanDatosOferta;

  constructor(private proxyOferta: ProxyOferta, private router: Router) {}

  enviarOferta(items: ItemOfertaDTO[]): void {
    const conValor = items.filter(i => i.valor > 0);
    if (!conValor.length) {
      this.pan?.mostrarMensaje('Escriba el valor de al menos una oferta');
      return;
    }
    this.proxyOferta.enviarOfertas({ items: conValor }).subscribe({
      next: r => this.pan?.mostrarMensaje(this.resumen(r)),
      error: e => {
        if (sesionVencida(e, this.router)) return;
        this.pan?.mostrarMensaje(mensajeDeError(e, 'No se pudo registrar la oferta'));
      }
    });
  }

  private resumen(r: RespuestaOfertaDTO): string {
    const nombre = (idSubasta: string) =>
      this.pan?.bienes.find(b => String(b.idSubasta) === idSubasta)?.nombreBien ?? 'subasta ' + idSubasta;
    const rechazos = Object.entries(r.rechazadas ?? {}).map(([idSubasta, motivo]) => `${nombre(idSubasta)}: ${motivo}`);
    return rechazos.length ? `${r.mensaje} No se aceptaron: ${rechazos.join('; ')}.` : r.mensaje;
  }
}
