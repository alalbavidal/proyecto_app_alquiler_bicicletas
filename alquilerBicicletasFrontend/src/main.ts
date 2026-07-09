
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import { LOCALE_ID } from '@angular/core';

registerLocaleData(localeEs);




bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),       // ✅ Esto es lo que faltaba
    provideRouter(routes),
    { provide: LOCALE_ID, useValue: 'es' }


  ],
});
