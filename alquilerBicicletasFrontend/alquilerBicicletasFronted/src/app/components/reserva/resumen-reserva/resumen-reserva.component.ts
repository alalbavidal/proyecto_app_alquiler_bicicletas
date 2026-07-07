import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Bicicleta } from '../../selectores/selector-bicicleta/selector-bicicleta.component';
import { CommonModule } from '@angular/common';
import { Tarifa } from '../../../models/tarifa.model';
import { AccesorioConCantidad } from '../../../models/accesorioConCantidad.model';

@Component({
  selector: 'app-resumen-reserva',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resumen-reserva.component.html',
  styleUrls: ['./resumen-reserva.component.css']
})
export class ResumenReservaComponent {
  @Input() fecha?: Date;
  @Input() tarifa?: Tarifa;
  @Input() bicicletas: Bicicleta[] = [];
  @Input() accesorios: AccesorioConCantidad[] = [];

  @Output() confirmarReserva = new EventEmitter<void>();
  @Output() eliminarBicicleta = new EventEmitter<number>();
  @Output() eliminarAccesorio = new EventEmitter<number>();

  // ✅ Usar un getter en lugar de ngOnInit
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

  onEliminarBicicleta(id: number) {
    this.eliminarBicicleta.emit(id);
  }

  onEliminarAccesorio(id: number) {
    this.eliminarAccesorio.emit(id);
  }
}