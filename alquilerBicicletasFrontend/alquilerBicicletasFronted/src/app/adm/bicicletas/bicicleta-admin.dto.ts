import { EstadoBicicleta } from './estado-bicicleta.enum';

export interface BicicletaAdminDTO {
  id?: number;
  modelo: string;
  numero: string;
  bastidor?: string;
  candado?: string;
  claveCandado?: string;
  descripcion?: string;
  imagenUrl?: string;
  estado: EstadoBicicleta;
  fechaCreacion?: string;
  fechaBaja?: string;
  observaciones?: string;
}
