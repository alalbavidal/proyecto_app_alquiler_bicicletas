import { Routes } from '@angular/router';
import {BicicletasListComponent} from './bicicletas/bicicletas-list/bicicletas-list.component';
import {BicicletaFormComponent} from './bicicletas/bicicleta-form/bicicleta-form.component';
import {AdminLayoutComponent} from './admin-layout/admin-layout.component';
import {AccesoriosListComponent} from './accesorios/accesorios-list/accesorios-list.component';
import {AccesorioFormComponent} from './accesorios/accesorio-form/accesorio-form.component';
import {TarifasListComponent} from './tarifas/tarifas-list/tarifas-list.component';
import {TarifaFormComponent} from './tarifas/tarifa-form/tarifa-form.component';
import {ReservasListComponent} from './reservas/reservas-list/reservas-list.component';
import {ProcesoReservaComponent} from '../components/reserva/proceso-reserva/proceso-reserva.component';


export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [

      // rutas de bicicletas
      { path: '', redirectTo: 'bicicletas', pathMatch: 'full' },
      { path: 'bicicletas', component: BicicletasListComponent },
      { path: 'bicicletas/new', component: BicicletaFormComponent },
      { path: 'bicicletas/:id/edit', component: BicicletaFormComponent },

      // Accesorios
      { path: 'accesorios', component: AccesoriosListComponent },
      { path: 'accesorios/new', component: AccesorioFormComponent },
      { path: 'accesorios/:id/edit', component: AccesorioFormComponent },


      // Tarifas

      { path: 'tarifas', component: TarifasListComponent },
      { path: 'tarifas/new', component: TarifaFormComponent },
      { path: 'tarifas/:id/edit', component: TarifaFormComponent },

      { path: 'reservas', component: ReservasListComponent },

      { path: 'calendario', component: ProcesoReservaComponent },






      // deja reservadas para después:
      // { path: 'accesorios', component: AccesoriosListComponent },
      // { path: 'reservas', component: ReservasCalendarComponent },
      // { path: 'clientes', component: ClientesListComponent },
      // { path: 'extras', component: ExtrasListComponent },
    ],
  },
];
