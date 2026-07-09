import { Component, Input, ViewChild, ChangeDetectorRef  } from '@angular/core';
import {  NgIf } from '@angular/common';

import { CalendarioReservaComponent } from '../calendario-reserva/calendario-reserva.component';
import { SelectorTarifaComponent } from '../../selectores/selector-tarifa/selector-tarifa.component';
import { Bicicleta, SelectorBicicletaComponent } from '../../selectores/selector-bicicleta/selector-bicicleta.component';
import { SelectorAccesorioComponent } from '../../selectores/selector-accesorio/selector-accesorio.component';
import { ResumenReservaComponent } from '../resumen-reserva/resumen-reserva.component';
import { ClienteFormComponent } from '../../formularios/cliente-form/cliente-form.component';
import { ToastComponent } from '../../toast/toast.component';

import { ReservaService } from '../../../services/reserva.service';
import { Cliente } from '../../../models/cliente.model';
import { DiaNoDisponible } from '../../../models/dia-no-disponible.model';
import { BicicletaDisponibleDTO } from '../../../models/bicicletaDisponibleDTO.model';
import { Tarifa } from '../../../models/tarifa.model';
import { ReservaConClienteRequest } from '../../../models/reservaConClienteRequest.model';
import { AccesorioUI, AccesorioConCantidadUI } from '../../../models/accesorioUI.model';
import { ReservaResponseDTO } from '../../../models/reservaResponse.model';

@Component({
  selector: 'app-proceso-reserva',
  standalone: true,
  imports: [
    CalendarioReservaComponent,
    SelectorTarifaComponent,
    SelectorBicicletaComponent,
    SelectorAccesorioComponent,
    ResumenReservaComponent,
    ClienteFormComponent,
    ToastComponent,
    NgIf,
    
  ],
  templateUrl: './proceso-reserva.component.html',
  
})
export class ProcesoReservaComponent {
  @ViewChild('toast') toast!: ToastComponent;

  diasNoDisponibles: DiaNoDisponible[] = [];

  fechaSeleccionada?: Date;
  tarifasDisponibles: Tarifa[] = [];
  tarifaSeleccionada?: Tarifa;

  // Bicis
  bicicletasDisponibles: BicicletaDisponibleDTO[] = [];
  bicicletasDisponiblesDTO: BicicletaDisponibleDTO[] = [];
  bicicletasParaSelector: Bicicleta[] = [];
  bicicletasSeleccionadas: Bicicleta[] = [];

  // Accesorios (UI)
  accesoriosDisponibles: AccesorioUI[] = [];
  accesoriosSeleccionados: AccesorioConCantidadUI[] = [];
  maxPorAccesorio: Record<number, number> = {};

  // Estado
  bicicletasCargadas = false;
  accesoriosCargados = false;

  // Cliente
  cliente?: Cliente;

  idiomaContrato: string = 'es';

  //Controlar el paso actual 
  pasoActual: number = 1; // 1: Fecha, 2: Tarifa y Bicis, 3: Accesorios, 4: Cliente y Resumen


  recargarResumen: boolean = true;


  constructor(
    private reservaService: ReservaService,  
    private cdr: ChangeDetectorRef ) {}

  

  onFechaSeleccionada(fecha: Date) {
  // Guardamos la fecha seleccionada
  this.fechaSeleccionada = fecha;
  
  // Solo reseteamos lo que depende de la fecha
  this.tarifaSeleccionada = undefined;
  this.tarifasDisponibles = [];
  this.bicicletasDisponibles = [];
  this.bicicletasDisponiblesDTO = [];
  this.bicicletasParaSelector = [];
  this.bicicletasSeleccionadas = [];
  this.accesoriosDisponibles = [];
  this.accesoriosSeleccionados = [];
  this.maxPorAccesorio = {};
  this.cliente = undefined;
  this.bicicletasCargadas = false;
  this.accesoriosCargados = false;
  
  // Cargar tarifas para la fecha seleccionada
  this.cargarTarifas(fecha);
  
  // Mantener el paso actual en 1 (o ir al paso 1 si no está)
  this.pasoActual = 1;
}

  cargarTarifas(fecha: Date) {
  const fechaStr = fecha.toISOString().split('T')[0];
  this.reservaService.obtenerTarifas(fechaStr).subscribe({
    next: (tarifas) => {
      this.tarifasDisponibles = tarifas;
      // Si hay tarifas, nos aseguramos de que el paso sea 1
      if (tarifas.length > 0) {
        this.pasoActual = 1;
      }
    },
    error: (err) => {
      console.error('Error al cargar tarifas:', err);
      this.tarifasDisponibles = [];
      this.toast.open({
        type: 'error',
        title: 'No se pudieron cargar las tarifas',
        message: 'Inténtalo de nuevo en unos segundos.',
        centered: true,
        backdrop: true,
        duration: 5000,
      });
    },
  });
}

  onTarifaSeleccionada(t: Tarifa) {
    if (!t) return;
    t.id = Number(t.id);
    this.tarifaSeleccionada = t;

    this.bicicletasSeleccionadas = [];
    this.accesoriosSeleccionados = [];
    this.accesoriosDisponibles = [];
    this.maxPorAccesorio = {};
    this.accesoriosCargados = false;

    if (this.fechaSeleccionada) {
      this.cargarBicicletasDisponibles(t, this.fechaSeleccionada);
    }

    this.pasoActual = 2; // Avanza al paso 2
  }

  cargarBicicletasDisponibles(tarifa: Tarifa, fecha: Date) {
    this.reservaService.obtenerBicicletas(fecha, tarifa.id).subscribe({
      next: (dtos) => {
        this.bicicletasDisponiblesDTO = dtos;
        this.bicicletasParaSelector = dtos.map((d) => ({
          id: d.bicicleta.id,
          modelo: d.bicicleta.modelo,
          tipo: d.bicicleta.tipo,
          precioDia: d.precioTarifa,
          numero: d.bicicleta.numero,
        }));
        this.bicicletasCargadas = true;
      },
      error: (error) => {
        console.error('Error al cargar bicicletas:', error);
        this.bicicletasCargadas = false;
        this.toast.open({
          type: 'error',
          title: 'No se pudieron cargar bicicletas',
          message: 'Revisa la conexión e inténtalo de nuevo.',
          centered: true,
          backdrop: true,
          duration: 5000,
        });
      },
    });
  }

  onBicicletasSeleccionadas(bicis: Bicicleta[]) {
    this.bicicletasSeleccionadas = [...bicis]; 
    this.accesoriosSeleccionados = [];
    this.cliente = undefined;
    this.cargarAccesorios(bicis);
  }

  cargarAccesorios(bicicletas: Bicicleta[]) {
    const numeroBicis = bicicletas.length;

    const base = this.bicicletasDisponiblesDTO[0];
    const fechaInicio = base?.fechaInicio;
    const fechaFin = base?.fechaFin;

    if (!fechaInicio || !fechaFin) {
      console.error('No hay fechaInicio/fechaFin para cargar opciones de accesorios');
      this.accesoriosDisponibles = [];
      this.maxPorAccesorio = {};
      this.accesoriosCargados = false;

      this.toast.open({
        type: 'warning',
        title: 'Falta información de la tarifa',
        message: 'Vuelve a seleccionar fecha y tarifa.',
        centered: true,
        backdrop: true,
        duration: 5000,
      });
      return;
    }

    this.reservaService
      .obtenerOpcionesAccesorios(fechaInicio, fechaFin, numeroBicis)
      .subscribe({
        next: (opciones) => {
          this.accesoriosDisponibles = opciones.map((o) => ({
            id: o.id,
            nombre: o.nombre,
            precio: o.precioDia,
          }));

          this.maxPorAccesorio = opciones.reduce((acc, o) => {
            acc[o.id] = o.maxPorReserva;
            return acc;
          }, {} as Record<number, number>);

          this.accesoriosCargados = true;
        },
        error: (err) => {
          console.error('Error al cargar opciones de accesorios', err);
          this.accesoriosDisponibles = [];
          this.maxPorAccesorio = {};
          this.accesoriosCargados = false;

          this.toast.open({
            type: 'error',
            title: 'No se pudieron cargar los accesorios',
            message: 'Prueba de nuevo en unos segundos.',
            centered: true,
            backdrop: true,
            duration: 5000,
          });
        },
      });
  }

  onAccesoriosSeleccionados(accesorios: AccesorioConCantidadUI[]) {
    this.accesoriosSeleccionados = [...accesorios]; 
    this.cliente = undefined;
  }

  onClienteConfirmado(clienteData: Cliente) {
    this.cliente = clienteData;
    console.log('Cliente confirmado:', clienteData);
    this.pasoActual = 4; // Mantiene el paso 4 (resumen final)
  }

  onConfirmarReserva() {
    if (!this.cliente) {
      this.toast.open({
        type: 'warning',
        title: 'Datos incompletos',
        message: 'Por favor, completa tus datos antes de confirmar.',
        centered: true,
        backdrop: true,
        duration: 5000,
      });
      return;
    }
    if (!this.fechaSeleccionada || !this.tarifaSeleccionada || this.bicicletasSeleccionadas.length === 0) {
      this.toast.open({
        type: 'warning',
        title: 'Faltan datos',
        message: 'Selecciona fecha, tarifa y al menos una bicicleta.',
        centered: true,
        backdrop: true,
        duration: 5000,
      });
      return;
    }

    const base = this.bicicletasDisponiblesDTO?.[0];
    if (!base?.fechaInicio || !base?.fechaFin) {
      this.toast.open({
        type: 'warning',
        title: 'Horario no disponible',
        message: 'Vuelve a seleccionar la fecha y la tarifa.',
        centered: true,
        backdrop: true,
        duration: 5000,
      });
      return;
    }

    const accesoriosPayload = (this.accesoriosSeleccionados || [])
      .map((a) => ({
        accesorioId: a.id,
        cantidad: Math.max(0, Number(a.cantidadSeleccionada ?? 1)),
      }))
      .filter((x) => x.cantidad > 0);

    const body: ReservaConClienteRequest = {
      cliente: this.cliente!,
      reserva: {
        tarifaId: this.tarifaSeleccionada!.id,
        fechaInicio: base.fechaInicio,
        fechaFin: base.fechaFin,
        bicicletas: this.bicicletasSeleccionadas.map((b) => ({ bicicletaId: b.id })),
        accesorios: accesoriosPayload,
      },
    };

    this.reservaService.crearReservaConCliente(body, this.idiomaContrato).subscribe({
      next: (res: ReservaResponseDTO) => {
        this.toast.open({
          type: 'success',
          title: this.idiomaContrato === 'es' ? '¡Reserva creada!' : 'Reservation created!',
          message: this.idiomaContrato === 'es'
            ? 'Hemos enviado la confirmación a tu correo, espere unos minutos.'
            : 'We have sent the confirmation to your email, please wait a few minutes.',
          actionLabel: this.idiomaContrato === 'es' ? 'Continuar' : 'Continue',
          duration: 0,     // no autocierra
          centered: true,
          backdrop: true,
        });
        this.resetearFormulario();
      },
      error: (error) => {
        const msg = error?.error?.error || error?.message || 'Error desconocido';
        this.toast.open({
          type: 'error',
          title: this.idiomaContrato === 'es' ? 'No se pudo crear la reserva' : 'Could not create reservation',
          message: msg,
          centered: true,
          backdrop: true,
          duration: 7000,
        });
        console.error(error);
      },
    });

    
  }
  cambiarIdioma(idioma: string) {
    this.idiomaContrato = idioma;
  }

  

  // ===== NUEVO MÉTODO PARA NAVEGACIÓN =====
  
 irAlPaso(paso: number) {
  // Validaciones existentes...
  if (paso === 2 && (!this.fechaSeleccionada || !this.tarifaSeleccionada)) {
    this.toast.open({
      type: 'warning',
      title: 'Paso incompleto',
      message: 'Primero debes seleccionar fecha y tarifa.',
      centered: true,
      backdrop: true,
      duration: 3000,
    });
    return;
  }
  
  if (paso === 3 && this.bicicletasSeleccionadas.length === 0) {
    this.toast.open({
      type: 'warning',
      title: 'Paso incompleto',
      message: 'Debes seleccionar al menos una bicicleta.',
      centered: true,
      backdrop: true,
      duration: 3000,
    });
    return;
  }
  
  // ✅ No necesitas recargar nada, el getter se actualiza solo
  this.pasoActual = paso;
}

  


  resetearFormulario() {
    this.fechaSeleccionada = undefined;
    this.tarifaSeleccionada = undefined;

    this.bicicletasDisponibles = [];
    this.bicicletasDisponiblesDTO = [];
    this.bicicletasParaSelector = [];
    this.bicicletasSeleccionadas = [];

    this.accesoriosDisponibles = [];
    this.accesoriosSeleccionados = [];
    this.maxPorAccesorio = {};
    this.accesoriosCargados = false;

    this.cliente = undefined;
  }

 onToastAction() {
   window.location.href = '';
 }



    /**
     * Elimina una bicicleta de la selección.
     * Si no quedan bicicletas, vuelve al paso 2.
     */

  onEliminarBicicleta(id: number) {
    this.bicicletasSeleccionadas = this.bicicletasSeleccionadas.filter(
      (bici: Bicicleta) => bici.id !== id
    );

    if (this.bicicletasSeleccionadas.length === 0) {
      this.irAlPaso(2);
      this.toast.open({
        type: 'info',
        title: 'No hay bicicletas seleccionadas',
        message: 'Selecciona al menos una bicicleta para continuar.',
        centered: true,
        backdrop: true,
        duration: 3000,
      });
    }
  }

  onEliminarAccesorio(id: number) {
    this.accesoriosSeleccionados = this.accesoriosSeleccionados.filter(
      (accesorio: AccesorioConCantidadUI) => accesorio.id !== id
    );
  }





}

