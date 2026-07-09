import { Component, EventEmitter, Input, Output } from '@angular/core'; // ← Eliminar OnChanges, SimpleChanges
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
export class ResumenReservaComponent { // ← Eliminar implements OnChanges

  @Input() fecha?: Date;
  @Input() tarifa?: Tarifa;
  @Input() bicicletas: Bicicleta[] = [];
  @Input() accesorios: AccesorioConCantidad[] = [];

  @Input() mostrarBotonConfirmar: boolean = true;

  @Output() confirmarReserva = new EventEmitter<void>();
  @Output() eliminarBicicleta = new EventEmitter<number>();
  @Output() eliminarAccesorio = new EventEmitter<number>();

  // ✅ IVA estándar en España (21%)
  private readonly IVA = 0.21;

  // ✅ Subtotal (sin IVA)
  get subtotal(): number {
    const totalBicis = (this.bicicletas ?? [])
      .reduce((sum, b) => sum + (b.precioDia ?? 0), 0);

    const totalAccs = (this.accesorios ?? [])
      .reduce((sum, a) => sum + ((a.precio ?? 0) * (a.cantidadSeleccionada ?? 1)), 0);

    return totalBicis + totalAccs;
  }

  // ✅ Total con IVA incluido
  get total(): number {
    return this.subtotal * (1 + this.IVA);
  }

  // ✅ IVA desglosado
  get ivaCalculado(): number {
    return this.subtotal * this.IVA;
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