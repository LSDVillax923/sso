import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { LoginDTO, TokenDTO } from '../dto';

export const CLAVE_TOKEN = 'sso.token';
export const CLAVE_NOMBRE = 'sso.nombre';

/** Llama a WSAutenticacion por NGINX (PC1:80) y guarda el JWT de la sesión. */
@Injectable({ providedIn: 'root' })
export class ProxyAutenticacion {

  urlApi = '/api/auth';

  constructor(private http: HttpClient) {}

  login(dto: LoginDTO): Observable<TokenDTO> {
    return this.http.post<TokenDTO>(this.urlApi + '/login', dto).pipe(
      tap(token => {
        sessionStorage.setItem(CLAVE_TOKEN, token.token);
        sessionStorage.setItem(CLAVE_NOMBRE, token.nombre);
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem(CLAVE_TOKEN);
    sessionStorage.removeItem(CLAVE_NOMBRE);
  }
}

export function haySesion(): boolean {
  return !!sessionStorage.getItem(CLAVE_TOKEN);
}
