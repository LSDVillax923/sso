import { bootstrapApplication } from '@angular/platform-browser';
import { configuracion } from './app/app.config';
import { SSOWebSPA } from './app/sso-web-spa';

bootstrapApplication(SSOWebSPA, configuracion).catch(err => console.error(err));
