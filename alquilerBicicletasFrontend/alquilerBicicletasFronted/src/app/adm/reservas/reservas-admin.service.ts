// src/app/adm/reservas/reservas-admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {  ReservaAdminDTO, EstadoReserva } from './reserva-admin.dto';
import {ExtraDTO, ReservaExtraLineDTO, TotalesExtrasDTO} from '../../models/extraDTO.model';

@Injectable({ providedIn: 'root' })
export class ReservasAdminService {
  private base = 'http://localhost:8085/api/adm/reservas';

  constructor(private http: HttpClient) {}


  // Listar todas las reservas sin filtros
  listar(): Observable<ReservaAdminDTO[]> {
    return this.http.get<ReservaAdminDTO[]>(this.base);
  }


  listarConFiltros(
    q: string,
    estado: EstadoReserva | "",
    pagado: boolean | null,
    fechaInicio: string | null,
    fechaFin: string | null,
    page = 0,
    size = 10
  ): Observable<{ content: ReservaAdminDTO[]; totalElements: number }> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (q) params = params.set('q', q);
    if (estado) params = params.set('estado', estado);
    if (pagado !== null) params = params.set('pagado', pagado.toString());
    if (fechaInicio) params = params.set('fechaInicio', fechaInicio);
    if (fechaFin) params = params.set('fechaFin', fechaFin);

    return this.http.get<{ content: ReservaAdminDTO[], totalElements: number }>(this.base, { params });
  }


  cancelar(id: number) {
    return this.http.put<ReservaAdminDTO>(`${this.base}/${id}/cancelar`, {});
  }

  eliminar(id: number) {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  pagada(id: number): Observable<ReservaAdminDTO> {
    return this.http.put<ReservaAdminDTO>(`${this.base}/${id}/pagar`, {});
  }


  tipoCobro(id: number, tipoCobro: string | null | undefined): Observable<ReservaAdminDTO> {
    let params = new HttpParams();
    if (tipoCobro != null) { // solo si no es null ni undefined
      params = params.set('tipoCobro', tipoCobro);
    }
    return this.http.put<ReservaAdminDTO>(`${this.base}/${id}/tipo-cobro`, null, { params });
  }


  actualizarObservaciones(id: number, observaciones: string): Observable<ReservaAdminDTO> {
    return this.http.put<ReservaAdminDTO>(`${this.base}/${id}/observaciones`, { observaciones });
  }


  // src/app/adm/reservas/reservas-admin.service.ts

// Listar todos los extras (opcionalmente filtrando por aplicable: RESERVA/DEVOLUCION/AMBOS)
  listarExtras(aplicable?: 'RESERVA' | 'DEVOLUCION' | 'AMBOS'): Observable<ExtraDTO[]> {
    let params = new HttpParams();
    if (aplicable) params = params.set('aplicable', aplicable);
    return this.http.get<ExtraDTO[]>(`http://localhost:8085/api/extras`, { params });
  }

// Listar extras de una reserva
  listarExtrasDeReserva(reservaId: number): Observable<ReservaExtraLineDTO[]> {
    return this.http.get<ReservaExtraLineDTO[]>(`http://localhost:8085/api/extras/reserva/${reservaId}`);
  }

// Agregar extra a una reserva
  agregarExtra(reservaId: number, extraId: number, cantidad: number, contexto: 'RESERVA' | 'DEVOLUCION' | 'AMBOS'): Observable<TotalesExtrasDTO> {
    return this.http.post<TotalesExtrasDTO>(`http://localhost:8085/api/extras/reserva/${reservaId}`, {
      extraId,
      cantidad,
      contexto
    });
  }

// Eliminar extra de una reserva
  eliminarExtra(reservaId: number, extraId: number): Observable<TotalesExtrasDTO> {
    return this.http.delete<TotalesExtrasDTO>(`http://localhost:8085/api/extras/reserva/${reservaId}/${extraId}`);
  }







}



