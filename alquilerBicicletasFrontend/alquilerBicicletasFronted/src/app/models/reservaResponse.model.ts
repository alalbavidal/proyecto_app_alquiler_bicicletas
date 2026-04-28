// models/reservaResponse.model.ts
export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | string;

export interface ReservaBicicletaDTO {
  bicicletaId: number;
  nombre: string;
}

export interface ReservaAccesorioDTO {
  accesorioId: number;
  cantidad: number;
  precioTotal: number; // BigDecimal en Java -> number aquí
  nombre: string;
}

export interface ReservaResponseDTO {
  id: number;
  clienteId: number;
  tarifaId: number;
  fechaInicio: string; // ISO del backend (LocalDateTime)
  fechaFin: string;    // ISO
  precioTotal: number; // total bicis + accesorios
  extrasTotal: number; // solo accesorios
  estado: EstadoReserva;
  contratoUrl: string;
  cancelToken: string;
  bicicletas: ReservaBicicletaDTO[];
  accesorios: ReservaAccesorioDTO[];
}
