import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemCarritoDTO } from '../dto';

/** Llama a WSCarrito por NGINX (PC1:80) y APIRestSSO. */
@Injectable({ providedIn: 'root' })
export class ProxyCarrito {

  urlApi = '/api/carrito';

  constructor(private http: HttpClient) {}

  agregar(idSubasta: number): Observable<void> {
    return this.http.post<void>(this.urlApi + '/' + idSubasta, null);
  }

  listar(): Observable<ItemCarritoDTO[]> {
    return this.http.get<ItemCarritoDTO[]>(this.urlApi);
  }

  quitar(idItem: number): Observable<void> {
    return this.http.delete<void>(this.urlApi + '/' + idItem);
  }
}
