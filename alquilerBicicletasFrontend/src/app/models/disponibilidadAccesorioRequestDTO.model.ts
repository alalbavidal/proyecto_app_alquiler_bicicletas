export interface DisponibilidadAccesorioRequestDTO {
  accesorios: { id: number; cantidad: number }[];
  fecha: string; // fecha de reserva, para validar disponibilidad en ese día
}
