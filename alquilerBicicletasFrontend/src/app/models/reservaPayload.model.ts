import {ReservaAccesorioPayload} from './reservaAccesorioPayload.model';
import {ReservaBicicletaPayload} from './reservaBicicletaPayload.model';

export interface ReservaPayload {
  tarifaId: number;
  fechaInicio: string; // ISO: "yyyy-MM-ddTHH:mm:ss"
  fechaFin: string;    // ISO
  bicicletas: ReservaBicicletaPayload[];
  accesorios: ReservaAccesorioPayload[];

  idiomaContrato?: 'es' | 'en'; // <- nuevo campo



}
