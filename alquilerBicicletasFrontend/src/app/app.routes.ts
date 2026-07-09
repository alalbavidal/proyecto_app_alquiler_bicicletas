import { Routes } from '@angular/router';
import { ProcesoReservaComponent } from './components/reserva/proceso-reserva/proceso-reserva.component';

import { HomeComponent } from './components/home/home.component';

export const routes: Routes = [
  // RUTA INICIAL 
  { path: '', component: HomeComponent },

  { path: 'calendario', component: ProcesoReservaComponent },

  {
    path: 'admin',
    loadChildren: () =>
      import('./adm/admin.routes').then(m => m.ADMIN_ROUTES),
  },
  {
    path: 'adm',
    loadChildren: () =>
      import('./adm/admin.routes').then(m => m.ADMIN_ROUTES),
  },

  // 👉 fallback correcto
  { path: '**', redirectTo: 'calendario' }



  
];