import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CurrencyPipe, NgForOf } from '@angular/common';
import {AccesorioConCantidadUI, AccesorioUI} from '../../../models/accesorioUI.model';

@Component({
  selector: 'app-selector-accesorio',
  standalone: true,
  templateUrl: './selector-accesorio.component.html',
  imports: [NgForOf, CurrencyPipe],
  styleUrls: ['./selector-accesorio.component.css']
})
export class SelectorAccesorioComponent implements OnChanges {
  @Input() accesorios: AccesorioUI[] = [];
  @Input() maxCantidad = 1;
  @Input() maxPorAccesorio: Record<number, number> = {};

  @Output() accesoriosSeleccionados = new EventEmitter<AccesorioConCantidadUI[]>();

  cantidades: Record<number, number> = {};

  ngOnChanges(changes: SimpleChanges): void {
    const idsActuales = new Set(this.accesorios.map(a => a.id));
    Object.keys(this.cantidades).forEach(idStr => {
      const id = Number(idStr);
      if (!idsActuales.has(id)) delete this.cantidades[id];
    });
    for (const acc of this.accesorios) {
      const max = this.getMax(acc);
      const cur = this.cantidades[acc.id] ?? 0;
      this.cantidades[acc.id] = Math.max(0, Math.min(cur, max));
    }
    this.emitirSeleccion();
  }

  getMax(acc: AccesorioUI): number {
    const global = this.maxCantidad ?? 0;
    const porAcc = this.maxPorAccesorio?.[acc.id];
    return Math.max(0, porAcc == null ? global : Math.min(global, porAcc));
  }

  onCantidadChange(accesorio: AccesorioUI, cantidad: number) {
    if (isNaN(cantidad) || cantidad < 0) cantidad = 0;
    const max = this.getMax(accesorio);
    if (cantidad > max) cantidad = max;
    this.cantidades[accesorio.id] = cantidad;
    this.emitirSeleccion();
  }

  emitirSeleccion() {
    const seleccionados: AccesorioConCantidadUI[] = this.accesorios
      .filter(a => (this.cantidades[a.id] || 0) > 0)
      .map(a => ({ ...a, cantidadSeleccionada: this.cantidades[a.id] }));
    this.accesoriosSeleccionados.emit(seleccionados);
  }
}
