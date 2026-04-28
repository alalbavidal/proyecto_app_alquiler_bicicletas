import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Bicicleta } from '../../selectores/selector-bicicleta/selector-bicicleta.component';
import { CommonModule, DatePipe } from '@angular/common';
import { Tarifa } from '../../../models/tarifa.model';
import {AccesorioConCantidad} from '../../../models/accesorioConCantidad.model';

@Component({
  selector: 'app-resumen-reserva',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './resumen-reserva.component.html',
  styleUrls: ['./resumen-reserva.component.css']
})
export class ResumenReservaComponent {
  @Input() fecha?: Date;
  @Input() tarifa?: Tarifa;
  @Input() bicicletas: Bicicleta[] = [];
  @Input() accesorios: AccesorioConCantidad[] = [];

  @Output() confirmarReserva = new EventEmitter<void>();

  get total(): number {
    const totalBicis = (this.bicicletas ?? [])
      .reduce((sum, b) => sum + (b.precioDia ?? 0), 0);

    const totalAccs = (this.accesorios ?? [])
      .reduce((sum, a) => sum + ((a.precio ?? 0) * (a.cantidadSeleccionada ?? 1)), 0);

    return totalBicis + totalAccs;
  }

  onConfirmar(): void {
    this.confirmarReserva.emit();
  }
}
