import { Component, EventEmitter, Input, Output } from '@angular/core';
import {CurrencyPipe, NgForOf} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Tarifa} from '../../../models/tarifa.model';



@Component({
  selector: 'app-selector-tarifa',
  standalone: true,
  templateUrl: './selector-tarifa.component.html',
  styleUrls: ['./selector-tarifa.component.css'],
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    FormsModule,
    NgForOf
  ]
})
export class SelectorTarifaComponent {
  @Input() tarifas: Tarifa[] = [];
  @Output() tarifaSeleccionadaChange = new EventEmitter<Tarifa>();

  tarifaSeleccionada: Tarifa | null = null;

  onTarifaSeleccionada() {
    if (this.tarifaSeleccionada) {
      this.tarifaSeleccionadaChange.emit(this.tarifaSeleccionada);
    }
  }
}
