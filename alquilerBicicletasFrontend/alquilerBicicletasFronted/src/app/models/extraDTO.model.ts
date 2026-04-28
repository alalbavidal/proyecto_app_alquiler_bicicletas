export interface ExtraDTO {
  id: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  aplicableA: string;
  restricciones: string;
}

export interface ReservaExtraLineDTO {
  id: number;
  extraId: number;
  nombre: string;
  cantidad: number;
  precioTotal: number;
  pagado: boolean;
  fechaAdicion: string;
}

export interface TotalesExtrasDTO {
  reservaId: number;
  extrasTotal: number;
  extras: ReservaExtraLineDTO[];
}
