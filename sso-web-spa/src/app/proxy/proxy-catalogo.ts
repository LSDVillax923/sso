import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BienDTO } from '../dto';

/** Llama a WSCatalogo por NGINX (PC1:80) y APIRestSSO. */
@Injectable({ providedIn: 'root' })
export class ProxyCatalogo {

  urlApi = '/api/catalogo';

  constructor(private http: HttpClient) {}

  buscarBienes(filtro: string): Observable<BienDTO[]> {
    return this.http.get<BienDTO[]>(this.urlApi, { params: new HttpParams().set('filtro', filtro ?? '') });
  }
}
