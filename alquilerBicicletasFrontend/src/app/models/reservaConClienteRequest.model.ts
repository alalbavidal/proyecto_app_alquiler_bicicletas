import {Cliente} from './cliente.model';
import {ReservaPayload} from './reservaPayload.model';

export interface ReservaConClienteRequest {
  cliente: Cliente;
  reserva: ReservaPayload;
}
