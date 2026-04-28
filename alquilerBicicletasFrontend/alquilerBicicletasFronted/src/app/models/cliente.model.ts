export type TipoDocumentoIdentidad = 'DNI' | 'NIE' | 'PASAPORTE' | 'OTRO';


export interface Cliente {
  nombre: string;
  apellido: string;
  email: string;
  fechaNacimiento: string;
  telefono?: string;
  tipoDocumento: TipoDocumentoIdentidad;   // ← NUEVO (obligatorio)
  documentoIdentidad: string;
  fotoDocumentoUrl: string;


}
