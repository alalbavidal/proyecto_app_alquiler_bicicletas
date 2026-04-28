export interface AccesorioUI {
  id: number;
  nombre: string;
  precio: number;              // precio mostrado en el selector
}

export interface AccesorioConCantidadUI extends AccesorioUI {
  cantidadSeleccionada: number;
}
