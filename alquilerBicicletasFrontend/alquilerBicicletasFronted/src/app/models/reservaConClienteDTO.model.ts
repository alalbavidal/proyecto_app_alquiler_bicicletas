import {Cliente} from './cliente.model';

export interface ReservaConClienteDTO {
  fecha: string; // en formato ISO: yyyy-MM-dd
  tarifaId: number;
  bicicletas: number[];  // IDs de las bicicletas
  accesorios: number[];  // IDs de los accesorios
  cliente: Cliente;      // Datos del cliente
}
