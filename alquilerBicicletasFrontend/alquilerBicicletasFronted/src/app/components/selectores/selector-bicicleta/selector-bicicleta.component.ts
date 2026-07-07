import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // ← Importa CommonModule

export interface Bicicleta {
  id: number;
  modelo: string;
  tipo: string;
  precioDia: number;
  numero: string;
  seleccionada?: boolean;
}

@Component({
  selector: 'app-selector-bicicleta',
  standalone: true,
  templateUrl: './selector-bicicleta.component.html',
  imports: [
    CommonModule // ← Con CommonModule tienes NgIf, NgFor y CurrencyPipe
  ],
  styleUrls: ['./selector-bicicleta.component.css']
})
export class SelectorBicicletaComponent implements OnInit {
  @Input() bicicletas: Bicicleta[] = [];
  @Input() seleccionadasIniciales: Bicicleta[] = []; 
  @Output() bicicletasSeleccionadas = new EventEmitter<Bicicleta[]>();

  seleccion: Set<number> = new Set();

  ngOnInit() {
    // Si hay selecciones iniciales, marcarlas como seleccionadas
    if (this.seleccionadasIniciales && this.seleccionadasIniciales.length > 0) {
      this.bicicletas.forEach(bici => {
        bici.seleccionada = this.seleccionadasIniciales.some(
          (seleccionada: Bicicleta) => seleccionada.id === bici.id
        );
        if (bici.seleccionada) {
          this.seleccion.add(bici.id);
        }
      });
      this.bicicletasSeleccionadas.emit(this.seleccionadasIniciales);
    }
  }

  toggleSeleccion(bici: Bicicleta) {
    if (this.seleccion.has(bici.id)) {
      this.seleccion.delete(bici.id);
      bici.seleccionada = false;
    } else {
      this.seleccion.add(bici.id);
      bici.seleccionada = true;
    }
    this.emitirSeleccion();
  }

  emitirSeleccion() {
    const seleccionArray = this.bicicletas.filter(b => this.seleccion.has(b.id));
    this.bicicletasSeleccionadas.emit(seleccionArray);
  }
}