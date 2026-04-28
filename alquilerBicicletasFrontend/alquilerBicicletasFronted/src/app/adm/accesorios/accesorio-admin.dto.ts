export interface AccesorioAdminDTO {
  id?: number;
  nombre: string;
  descripcion?: string;
  precioDia: number;
  stock: number;
  observaciones?: string;
  activo: boolean;
}
