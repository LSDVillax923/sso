import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CompraDTO, PeticionCompraDTO, RespuestaCompraDTO } from '../dto';

/** Llama a WSCompra por RutasGateway, BalanceadorSSO y APIRestSSO (HTTPS 8443). */
@Injectable({ providedIn: 'root' })
export class ProxyCompra {

  urlApi = '/api/compras';

  constructor(private http: HttpClient) {}

  iniciarCompra(idOferta: number): Observable<RespuestaCompraDTO> {
    const peticion: PeticionCompraDTO = { idOferta };
    return this.http.post<RespuestaCompraDTO>(this.urlApi, peticion);
  }

  consultar(numeroCompra: number): Observable<CompraDTO> {
    return this.http.get<CompraDTO>(this.urlApi + '/' + numeroCompra);
  }
}
