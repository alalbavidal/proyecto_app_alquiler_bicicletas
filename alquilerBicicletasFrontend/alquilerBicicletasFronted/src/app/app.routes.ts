import { Routes } from '@angular/router';
import { ProcesoReservaComponent } from './components/reserva/proceso-reserva/proceso-reserva.component';

export const routes: Routes = [
  // 👉 RUTA INICIAL REAL DE TU APP
  { path: '', redirectTo: 'calendario', pathMatch: 'full' },

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