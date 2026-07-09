import { Component, OnInit, inject, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservasAdminService } from '../reservas-admin.service';
import { ReservaAdminDTO, EstadoReserva } from '../reserva-admin.dto';
import { forkJoin, Observable } from 'rxjs';
import {ExtraDTO, ReservaExtraLineDTO, TotalesExtrasDTO} from '../../../models/extraDTO.model';

@Component({
  standalone: true,
  selector: 'app-reservas-list',
  imports: [CommonModule, FormsModule, DatePipe, CurrencyPipe],
  templateUrl: './reservas-list.component.html',
  styleUrls: ['./reservas-list.component.css']
})
export class ReservasListComponent implements OnInit {
  private srv = inject(ReservasAdminService);
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);

  reservas: ReservaAdminDTO[] = [];
  loading = false;
  error = '';

  reservaSeleccionada?: ReservaAdminDTO;
  dialogAbierto = false;
  dialogExtrasAbierto = false;
  observaciones: string = '';

  // Filtros
  q: string = '';
  estado: EstadoReserva | '' = '';
  pagado: boolean | null = null;
  fechaInicio: string = '';
  fechaFin: string = '';

  // Extras
  extrasDisponibles: ExtraDTO[] = [];
  extrasSeleccionadosMap: { [extraId: number]: number } = {};
  reservaExtrasTotales: number = 0;

  // Guardados y nuevos
  extrasGuardados: { extra: ExtraDTO, cantidad: number }[] = [];
  extrasNuevos: { extra: ExtraDTO, cantidad: number }[] = [];

  // Paginación
  page = 0;
  size = 10;
  totalElements = 0;

  ngOnInit(): void {
    this.aplicarFiltros();
  }

  // ===========================
  // Filtros y paginación
  // ===========================
  aplicarFiltros() {
    this.loading = true;
    this.error = '';

    const fechaInicioISO = this.fechaInicio
      ? this.fechaInicio.split('/').reverse().join('-') + 'T00:00:00'
      : null;
    const fechaFinISO = this.fechaFin
      ? this.fechaFin.split('/').reverse().join('-') + 'T23:59:59'
      : null;

    this.srv.listarConFiltros(this.q, this.estado, this.pagado, fechaInicioISO, fechaFinISO, this.page, this.size)
      .subscribe({
        next: res => {
          this.reservas = res.content;
          this.reservas.forEach(r => {
            this.srv.listarExtrasDeReserva(r.id).subscribe({
              next: extras => {
                r.tieneExtras = extras && extras.length > 0;
              },
              error: () => r.tieneExtras = false
            });
          });
          this.totalElements = res.totalElements;
          this.marcarReservasConExtras();
          this.loading = false;
        },
        error: e => {
          this.error = e?.error || 'Error aplicando filtros';
          this.loading = false;
        }
      });
  }

  cambiarPagina(nuevaPagina: number) {
    this.page = nuevaPagina;
    this.aplicarFiltros();
  }

  limpiarFiltros() {
    this.q = '';
    this.estado = '';
    this.pagado = null;
    this.fechaInicio = '';
    this.fechaFin = '';
    this.page = 0;
    this.aplicarFiltros();
  }

  // ===========================
  // Contratos y acciones
  // ===========================
  verContrato(url?: string | null) {
    if (!url) return;
    const absolute = url.startsWith('http') ? url : `http://localhost:8085${url}`;
    window.open(absolute, '_blank');
  }

  cancelar(r: ReservaAdminDTO) {
    if (r.estado === 'CANCELADA') return;
    if (!confirm(`¿Cancelar la reserva #${r.id}?`)) return;

    this.srv.cancelar(r.id).subscribe({
      next: upd => {
        this.reservas = this.reservas.map(x => x.id === r.id ? { ...x, ...upd } : x);
        this.cdr.detectChanges();
      },
      error: e => alert(e?.error?.mensaje || 'No se pudo cancelar')
    });
  }

  eliminar(r: ReservaAdminDTO) {
    if (!confirm(`¿Eliminar la reserva #${r.id}?`)) return;

    this.srv.eliminar(r.id).subscribe({
      next: () => this.reservas = this.reservas.filter(x => x.id !== r.id),
      error: e => alert(e?.error?.mensaje || 'No se pudo eliminar')
    });
  }

  pagada(r: ReservaAdminDTO) {
    const accion = r.pagado ? 'marcar como NO pagada' : 'marcar como pagada';
    if (!confirm(`¿Quieres ${accion} la reserva #${r.id}?`)) return;

    const old = { ...r };
    this.reservas = this.reservas.map(x => x.id === r.id ? { ...x, pagado: !x.pagado } : x);

    this.srv.pagada(r.id).subscribe({
      next: upd => {
        this.reservas = this.reservas.map(x => x.id === r.id ? { ...x, ...upd } : x);
        this.ngZone.run(() => this.cdr.detectChanges());
      },
      error: e => {
        this.reservas = this.reservas.map(x => x.id === r.id ? old : x);
        this.cdr.detectChanges();
        alert(e?.error?.mensaje || 'No se pudo actualizar');
      }
    });
  }

  tipoCobro(id: number, tipoCobro: string | null | undefined) {
    return this.srv.tipoCobro(id, tipoCobro).subscribe({
      next: upd => this.reservas = this.reservas.map(x => x.id === id ? { ...x, ...upd } : x),
      error: e => alert(e?.error?.mensaje || 'No se pudo actualizar el tipo de cobro')
    });
  }

  trackByReserva(index: number, item: ReservaAdminDTO) {
    return item.id;
  }

  protected readonly Math = Math;

  // ===========================
  // Observaciones
  // ===========================
  abrirObservaciones(r: ReservaAdminDTO) {
    this.observaciones = r.observaciones ?? '';
    this.reservaSeleccionada = r;
    this.dialogAbierto = true;
  }

  guardarObservaciones() {
    if (!this.reservaSeleccionada) return;

    this.srv.actualizarObservaciones(this.reservaSeleccionada.id, this.observaciones)
      .subscribe({
        next: upd => {
          this.reservas = this.reservas.map(x => x.id === upd.id ? upd : x);
          this.cerrarDialog();
        },
        error: e => alert(e?.error?.mensaje || 'No se pudo guardar')
      });
  }

  cerrarDialog() {
    this.dialogAbierto = false;
    this.reservaSeleccionada = undefined;
  }

  // ===========================
  // Extras
  // ===========================
  abrirDialogoExtras(reserva: ReservaAdminDTO) {
    if (!reserva) return;

    this.reservaSeleccionada = reserva;
    this.dialogExtrasAbierto = true;

    // Reseteamos mapa y totales
    this.extrasSeleccionadosMap = {};
    this.reservaExtrasTotales = 0;
    this.extrasGuardados = [];
    this.extrasNuevos = [];

    // 1️⃣ Traemos extras disponibles
    this.srv.listarExtras().subscribe({
      next: (extras: ExtraDTO[]) => {
        this.extrasDisponibles = extras;

        // 2️⃣ Traemos los extras guardados de la reserva
        this.srv.listarExtrasDeReserva(reserva.id).subscribe({
          next: (lineas: ReservaExtraLineDTO[]) => {
            if (!lineas || lineas.length === 0) return;

            this.extrasGuardados = lineas.map(l => ({
              extra: this.extrasDisponibles.find(e => e.id === l.extraId) || {
                id: l.extraId,
                nombre: l.nombre,
                precio: l.cantidad > 0 ? l.precioTotal / l.cantidad : 0,
                aplicableA: 'RESERVA',
                restricciones: ''
              },
              cantidad: l.cantidad
            }));

            // Inicializamos mapa de cantidades
            this.extrasGuardados.forEach(e => {
              this.extrasSeleccionadosMap[e.extra.id] = e.cantidad;
            });

            this.calcularTotales();
          },
          error: e => alert(e?.error?.mensaje || 'No se pudieron cargar los extras de la reserva')
        });
      },
      error: e => alert(e?.error?.mensaje || 'No se pudieron cargar los extras disponibles')
    });
  }






  getCantidad(extra: ExtraDTO): number {
    return this.extrasSeleccionadosMap[extra.id] ?? 0;
  }

  seleccionarExtra(extra: ExtraDTO, cantidad: number) {
    if (cantidad <= 0) {
      delete this.extrasSeleccionadosMap[extra.id];
    } else {
      this.extrasSeleccionadosMap[extra.id] = cantidad;
    }
    this.calcularTotales();
  }

  calcularTotales() {
    this.reservaExtrasTotales = Object.entries(this.extrasSeleccionadosMap)
      .reduce((acc, [id, cantidad]) => {
        const extra = this.extrasDisponibles.find(e => e.id === +id);
        return acc + ((extra?.precio ?? 0) * cantidad);
      }, 0);
  }

  guardarExtras() {
    if (!this.reservaSeleccionada) return;

    const observables: Observable<TotalesExtrasDTO>[] = [];

    // Convertir el mapa en array
    Object.entries(this.extrasSeleccionadosMap).forEach(([id, cantidad]) => {
      observables.push(this.srv.agregarExtra(this.reservaSeleccionada!.id, +id, cantidad, 'RESERVA'));
    });

    // Detectar extras eliminados (los guardados que ya no están en el mapa)
    this.extrasGuardados.forEach(e => {
      if (!(e.extra.id in this.extrasSeleccionadosMap)) {
        observables.push(this.srv.eliminarExtra(this.reservaSeleccionada!.id, e.extra.id));
      }
    });

    if (observables.length === 0) {
      this.dialogExtrasAbierto = false;
      return;
    }

    forkJoin(observables).subscribe({
      next: () => {
        this.extrasGuardados = Object.entries(this.extrasSeleccionadosMap).map(([id, cantidad]) => ({
          extra: this.extrasDisponibles.find(e => e.id === +id)!,
          cantidad
        }));
        this.extrasNuevos = [];
        this.dialogExtrasAbierto = false;
        this.calcularTotales();

        // Actualizar color de la lupa
        this.reservaSeleccionada!.tieneExtras = this.extrasGuardados.length > 0;

        alert('Extras guardados correctamente');
      },

    });

  }

  private marcarReservasConExtras() {
    this.reservas.forEach(r => {
      r.tieneExtras = false; // por defecto
      this.srv.listarExtrasDeReserva(r.id).subscribe({
        next: extras => r.tieneExtras = extras && extras.length > 0,
        error: () => r.tieneExtras = false
      });
    });
  }

}
