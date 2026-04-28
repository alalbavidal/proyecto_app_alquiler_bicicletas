import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {DiaNoDisponible} from '../models/dia-no-disponible.model';

@Injectable({ providedIn: 'root' })
export class DisponibilidadService {

  private apiUrl = 'http://localhost:8085/api/disponibilidad';

  constructor(private http: HttpClient) {}

  obtenerDiasNoDisponibles(mes: number, anio: number): Observable<DiaNoDisponible[]> {
    const params = new HttpParams()
      .set('mes', mes.toString())
      .set('anio', anio.toString());

    return this.http.get<DiaNoDisponible[]>(`${this.apiUrl}/calendario`, { params });
  }
}
