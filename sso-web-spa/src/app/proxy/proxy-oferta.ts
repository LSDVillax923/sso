import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OfertaDTO, PeticionOfertaDTO, RespuestaOfertaDTO } from '../dto';

/** Llama a WSOferta por RutasGateway, BalanceadorSSO y APIRestSSO (HTTPS 8443). */
@Injectable({ providedIn: 'root' })
export class ProxyOferta {

  urlApi = '/api/ofertas';

  constructor(private http: HttpClient) {}

  enviarOfertas(p: PeticionOfertaDTO): Observable<RespuestaOfertaDTO> {
    return this.http.post<RespuestaOfertaDTO>(this.urlApi, p);
  }

  ganadoras(): Observable<OfertaDTO[]> {
    return this.http.get<OfertaDTO[]>(this.urlApi + '/ganadoras');
  }
}
