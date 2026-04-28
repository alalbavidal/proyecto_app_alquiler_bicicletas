import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Tarifa} from '../models/tarifa.model';
import {BicicletaDisponibleDTO} from '../models/bicicletaDisponibleDTO.model';
import {formatDate} from '@angular/common';
import {AccesorioOpcion} from '../models/accesorioOpcion.model';
import {AccesorioDisponibilidadResponse} from '../models/accesorioDisponibilidad.model';
import {ReservaConClienteRequest} from '../models/reservaConClienteRequest.model';
import {ReservaResponseDTO} from '../models/reservaResponse.model';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private apiUrl = 'http://localhost:8085/api';
  constructor(private http: HttpClient) {}

  obtenerTarifas(fecha: string): Observable<Tarifa[]> {
    return this.http.get<Tarifa[]>(`${this.apiUrl}/tarifas?fecha=${fecha}`);
  }

  obtenerBicicletas(fecha: Date | string, tarifaId: number): Observable<BicicletaDisponibleDTO[]> {
    const fechaStr = typeof fecha === 'string' ? fecha : formatDate(fecha, 'yyyy-MM-dd', 'es-ES');
    const params = new HttpParams().set('fecha', fechaStr).set('tarifaId', tarifaId.toString());
    return this.http.get<BicicletaDisponibleDTO[]>(`${this.apiUrl}/bicicletas/disponibles`, { params });
  }

  obtenerOpcionesAccesorios(fechaInicio: Date | string, fechaFin: Date | string, numeroBicis: number): Observable<AccesorioOpcion[]> {
    const inicioStr = typeof fechaInicio === 'string' ? fechaInicio : formatDate(fechaInicio, "yyyy-MM-dd'T'HH:mm:ss", 'es-ES');
    const finStr    = typeof fechaFin    === 'string' ? fechaFin    : formatDate(fechaFin,    "yyyy-MM-dd'T'HH:mm:ss", 'es-ES');

    const params = new HttpParams()
      .set('fechaInicio', inicioStr)
      .set('fechaFin', finStr)
      .set('numeroBicis', numeroBicis.toString());

    return this.http.get<AccesorioOpcion[]>(`${this.apiUrl}/accesorios/opciones`, { params });
  }

  verificarDisponibilidadAccesorios(
    fechaInicio: Date | string,
    fechaFin: Date | string,
    accesorios: { accesorioId: number; cantidad: number }[]
  ): Observable<AccesorioDisponibilidadResponse> {
    const body = {
      fechaInicio: typeof fechaInicio === 'string' ? fechaInicio : formatDate(fechaInicio, "yyyy-MM-dd'T'HH:mm:ss", 'es-ES'),
      fechaFin:    typeof fechaFin    === 'string' ? fechaFin    : formatDate(fechaFin,    "yyyy-MM-dd'T'HH:mm:ss", 'es-ES'),
      accesorios
    };
    return this.http.post<AccesorioDisponibilidadResponse>(`${this.apiUrl}/accesorios/disponibilidad`, body);
  }

  crearReservaConCliente(body: ReservaConClienteRequest, idioma: string = 'es'): Observable<ReservaResponseDTO> {
    const params = new HttpParams().set('idioma', idioma);
    return this.http.post<ReservaResponseDTO>(`${this.apiUrl}/reservas/con-cliente`, body, { params });
  }

}
