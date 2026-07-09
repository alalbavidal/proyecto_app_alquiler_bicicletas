import {BicicletaDTO} from './bicicletaDTO.model';

export interface BicicletaDisponibleDTO {
  bicicleta: BicicletaDTO;
  precioTarifa: number;
  horarioTarifa: string;
  fechaInicio: string; // ISO string
  fechaFin: string;
}
