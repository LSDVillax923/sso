import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CompraDTO, EstadoCompra } from '../../dto';
import { CtrlEstadoCompra } from './ctrl-estado-compra';

const EXPLICACION: Record<EstadoCompra, string> = {
  VERIFICANDO_OFAC: 'Estamos validando su identidad. Le llegará un SMS cuando pueda pagar.',
  RECHAZADA_OFAC: 'No es posible continuar: su identificación no está habilitada para compras.',
  PENDIENTE_PAGO: 'Pague en YoPago con el enlace que le llegó por SMS.',
  DISTRIBUIDA: 'Enviamos la compra a inventarios y envíos; esperamos su confirmación.',
  TERMINADA: 'Compra realizada. El envío quedó programado a su dirección registrada.'
};

/** CU5 a CU7 · estado de la compra, con el valor total con impuestos. */
@Component({
  selector: 'sso-pan-estado-compra',
  standalone: true,
  imports: [CurrencyPipe],
  providers: [CtrlEstadoCompra],
  template: `
    <section class="tarjeta angosta">
      <h1>Compra {{ numeroCompra }}</h1>
      @if (aviso) { <p class="error">{{ aviso }}</p> }
      @if (compra) {
        <p class="estado">{{ compra.estado.split('_').join(' ') }}</p>
        <p>{{ explicacion[compra.estado] }}</p>
        <dl>
          <dt>Bien</dt><dd>{{ compra.nombreBien }}</dd>
          <dt>Valor de la oferta</dt><dd>{{ compra.valorOferta | currency:'COP':'symbol-narrow':'1.0-0' }}</dd>
          <dt>Impuestos</dt><dd>{{ compra.impuestos | currency:'COP':'symbol-narrow':'1.0-0' }}</dd>
          <dt>Total a pagar</dt><dd><b>{{ compra.valorTotal | currency:'COP':'symbol-narrow':'1.0-0' }}</b></dd>
        </dl>
      }
      <button class="primario" (click)="ctrlEstadoCompra.consultar(numeroCompra)">Actualizar</button>
    </section>
  `
})
export class PanEstadoCompra implements OnInit {

  compra?: CompraDTO;
  aviso = '';
  numeroCompra = 0;
  explicacion = EXPLICACION;

  constructor(public ctrlEstadoCompra: CtrlEstadoCompra, private ruta: ActivatedRoute) {
    ctrlEstadoCompra.pan = this;
  }

  ngOnInit(): void {
    this.numeroCompra = Number(this.ruta.snapshot.paramMap.get('numero'));
    this.ctrlEstadoCompra.consultar(this.numeroCompra);
  }

  mostrarEstado(compra: CompraDTO): void {
    this.aviso = '';
    this.compra = compra;
  }
}
