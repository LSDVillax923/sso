import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import localeEsCo from '@angular/common/locales/es-CO';
import { ApplicationConfig, LOCALE_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { rutas } from './app.routes';
import { InterceptorJWT } from './proxy/interceptor-jwt';

registerLocaleData(localeEsCo);

export const configuracion: ApplicationConfig = {
  providers: [
    provideRouter(rutas),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: InterceptorJWT, multi: true },
    { provide: LOCALE_ID, useValue: 'es-CO' }
  ]
};
