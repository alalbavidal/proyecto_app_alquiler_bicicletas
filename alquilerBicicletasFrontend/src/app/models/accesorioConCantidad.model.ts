import {Accesorio} from './accesorio.model';

export interface AccesorioConCantidad extends Accesorio {
  cantidadSeleccionada: number;
}
