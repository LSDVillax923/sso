import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CLAVE_TOKEN } from './proxy-autenticacion';

/** Agrega la cabecera Authorization: Bearer con el JWT de la sesión a cada llamada al API. */
@Injectable()
export class InterceptorJWT implements HttpInterceptor {

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = sessionStorage.getItem(CLAVE_TOKEN);
    if (!token || !req.url.startsWith('/api/')) {
      return next.handle(req);
    }
    return next.handle(req.clone({ setHeaders: { Authorization: 'Bearer ' + token } }));
  }
}
