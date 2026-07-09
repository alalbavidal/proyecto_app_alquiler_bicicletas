// src/app/adm/tarifas/tarifa-admin.dto.ts
export interface TarifaAdminDTO {
  id?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  horasIncluidas?: number | null;
  diasMinimos?: number | null;
  precioPorDia: boolean;
  activa: boolean;
  horaInicio?: string | null; // "HH:mm:ss"
  horaFin?: string | null;    // "HH:mm:ss"
}
