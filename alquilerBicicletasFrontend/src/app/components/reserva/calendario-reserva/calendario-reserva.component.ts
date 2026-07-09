import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {NgbDatepickerModule, NgbDateStruct, NgbCalendar} from '@ng-bootstrap/ng-bootstrap';
import { DisponibilidadService } from '../../../services/disponibilidad.service';
import { ReservaService } from '../../../services/reserva.service';
import { DiaNoDisponible } from '../../../models/dia-no-disponible.model';
import { Tarifa } from '../../../models/tarifa.model';
import { FormsModule } from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-calendario-reserva',
  standalone: true,
  templateUrl: './calendario-reserva.component.html',
  styleUrls: ['./calendario-reserva.component.css'],
  imports: [

    FormsModule,
    NgbDatepickerModule,
    NgIf,
    NgForOf,
  ]
})
export class CalendarioReservaComponent implements OnInit {
  @Input() diasNoDisponibles: DiaNoDisponible[] = [];
  @Output() fechaSeleccionada = new EventEmitter<Date>();

  tarifasDisponibles: Tarifa[] = [];
  tarifaSeleccionadaId?: number;

  fechaActual: NgbDateStruct = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1,
    day: new Date().getDate()
  };

  // Fecha mostrada en el calendario (para navegación)
  fechaMostrada: NgbDateStruct = { ...this.fechaActual };

  // Nombres de los meses en español
  nombresMeses = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
  ];

  constructor(
    private disponibilidadService: DisponibilidadService,
    private reservaService: ReservaService,
    private calendar: NgbCalendar
  ) {}

  ngOnInit(): void {
    this.cargarDiasNoDisponibles();
  }

  private cargarDiasNoDisponibles(): void {
    const { year, month } = this.fechaMostrada;
    this.disponibilidadService.obtenerDiasNoDisponibles(month, year)
      .subscribe(dias => {
        this.diasNoDisponibles = dias;
      });
  }

  // Navegación manual del calendario
  mesAnterior(): void {
    if (this.fechaMostrada.month === 1) {
      this.fechaMostrada = {
        year: this.fechaMostrada.year - 1,
        month: 12,
        day: 1
      };
    } else {
      this.fechaMostrada = {
        ...this.fechaMostrada,
        month: this.fechaMostrada.month - 1,
        day: 1
      };
    }
    this.cargarDiasNoDisponibles();
  }

  mesSiguiente(): void {
    if (this.fechaMostrada.month === 12) {
      this.fechaMostrada = {
        year: this.fechaMostrada.year + 1,
        month: 1,
        day: 1
      };
    } else {
      this.fechaMostrada = {
        ...this.fechaMostrada,
        month: this.fechaMostrada.month + 1,
        day: 1
      };
    }
    this.cargarDiasNoDisponibles();
  }

  // Obtener el nombre del mes actual
  get nombreMesActual(): string {
    return this.nombresMeses[this.fechaMostrada.month - 1];
  }

  // Obtener el año actual
  get anioActual(): number {
    return this.fechaMostrada.year;
  }

  onFechaSeleccionada(model: NgbDateStruct) {
    const fecha = new Date(model.year, model.month - 1, model.day);
    this.fechaMostrada = { year: model.year, month: model.month, day: model.day }; // Actualizar fechaMostrada
    this.fechaSeleccionada.emit(fecha);
  }

  esFechaRoja(fecha: NgbDateStruct): boolean {
    const hoy = new Date();
    const date = new Date(fecha.year, fecha.month - 1, fecha.day);

    const fechaStr = `${fecha.year}-${String(fecha.month).padStart(2, '0')}-${String(fecha.day).padStart(2, '0')}`;

    return this.diasNoDisponibles.some(d => d.fecha === fechaStr) || date < new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());
  }

  esDiaDeshabilitado = (fecha: NgbDateStruct): boolean => {
    const hoy = new Date();
    const fechaActual = new Date(fecha.year, fecha.month - 1, fecha.day);

    const esPasado = fechaActual < new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());    const esDomingo = fechaActual.getDay() === 0;
    const esNoDisponible = this.esFechaRoja(fecha);

    return esPasado || esDomingo || esNoDisponible;
  };

  isSunday(fecha: NgbDateStruct): boolean {
    const date = new Date(fecha.year, fecha.month - 1, fecha.day);
    return date.getDay() === 0;
  }

  isToday(date: any): boolean {
    const today = new Date();

    return date.year === today.getFullYear() &&
      date.month === (today.getMonth() + 1) &&
      date.day === today.getDate();
  }
}
