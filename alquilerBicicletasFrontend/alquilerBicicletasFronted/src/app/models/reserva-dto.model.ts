export interface ReservaDTO {
  fecha: string; // ISO date string, ej. '2025-05-15'
  tarifaId: number;
  bicicletasIds: number[];
  accesoriosIds: number[];
  cliente?: {
    nombre: string;
    email: string;
    telefono?: string;
  }
}
