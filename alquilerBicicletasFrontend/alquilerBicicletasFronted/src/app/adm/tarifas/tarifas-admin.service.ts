// src/app/adm/tarifas/tarifas-admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TarifaAdminDTO } from './tarifa-admin.dto';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TarifasAdminService {
  private base = 'http://localhost:8085/api/admin/tarifas';

  constructor(private http: HttpClient) {}

  listar(): Observable<TarifaAdminDTO[]> {
    return this.http.get<TarifaAdminDTO[]>(this.base);
  }
  obtener(id: number): Observable<TarifaAdminDTO> {
    return this.http.get<TarifaAdminDTO>(`${this.base}/${id}`);
  }
  crear(dto: TarifaAdminDTO): Observable<TarifaAdminDTO> {
    return this.http.post<TarifaAdminDTO>(this.base, dto);
  }
  actualizar(id: number, dto: TarifaAdminDTO): Observable<TarifaAdminDTO> {
    return this.http.put<TarifaAdminDTO>(`${this.base}/${id}`, dto);
  }
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
  setActiva(id: number, value: boolean): Observable<TarifaAdminDTO> {
    return this.http.patch<TarifaAdminDTO>(`${this.base}/${id}/activa?value=${value}`, {});
  }
}
