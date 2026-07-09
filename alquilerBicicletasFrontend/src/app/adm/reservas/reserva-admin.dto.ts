// src/app/adm/reservas/reserva-admin.dto.ts
import {ReservaExtraLineDTO, TotalesExtrasDTO} from '../../models/extraDTO.model';

export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';

export interface ReservaAdminDTO {
  id: number;
  clienteNombre: string | null;
  clienteApellido: string | null;
  clienteTelefono: string | null;
  clienteEmail: string | null;
  fechaInicio: string; // ISO
  fechaFin: string;    // ISO
  precioTotal: number | null;
  extrasTotal: number | null;
  estado: EstadoReserva;
  pagado: boolean;
  contratoUrl?: string | null;
  observaciones?: string | null;
  tipoCobro: 'EFECTIVO' | 'TPV' | 'TRANSFERENCIA' | null;
  extras?: ReservaExtraLineDTO[];

  // Nuevo campo temporal
  tieneExtras?: boolean;

}
