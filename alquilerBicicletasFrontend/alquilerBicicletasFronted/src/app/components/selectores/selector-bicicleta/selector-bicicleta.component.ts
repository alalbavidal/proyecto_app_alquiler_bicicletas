import { Component, EventEmitter, Input, Output } from '@angular/core';
import {CurrencyPipe, NgForOf} from '@angular/common';

export interface Bicicleta {
  id: number;
  modelo: string;
  tipo: string;
  precioDia: number;
  numero: string;
}

@Component({
  selector: 'app-selector-bicicleta',
  standalone: true,
  templateUrl: './selector-bicicleta.component.html',
  imports: [
    NgForOf,
    CurrencyPipe
  ],
  styleUrls: ['./selector-bicicleta.component.css']
})
export class SelectorBicicletaComponent {
  @Input() bicicletas: Bicicleta[] = [];
  @Output() bicicletasSeleccionadas = new EventEmitter<Bicicleta[]>();

  seleccion: Set<number> = new Set();

  toggleSeleccion(bici: Bicicleta) {
    if (this.seleccion.has(bici.id)) {
      this.seleccion.delete(bici.id);
    } else {
      this.seleccion.add(bici.id);
    }
    this.emitirSeleccion();
  }

  emitirSeleccion() {
    const seleccionArray = this.bicicletas.filter(b => this.seleccion.has(b.id));
    this.bicicletasSeleccionadas.emit(seleccionArray);
  }
}
