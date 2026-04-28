import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BicicletaAdminDTO } from './bicicleta-admin.dto';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BicicletasAdminService {
  private base = '/api/bicicletas';

  constructor(private http: HttpClient) {}

  listar(): Observable<BicicletaAdminDTO[]> {
    return this.http.get<BicicletaAdminDTO[]>(this.base);
  }
  obtener(id: number): Observable<BicicletaAdminDTO> {
    return this.http.get<BicicletaAdminDTO>(`${this.base}/${id}`);
  }
  crear(payload: BicicletaAdminDTO): Observable<BicicletaAdminDTO> {
    return this.http.post<BicicletaAdminDTO>(this.base, payload);
  }
  actualizar(id: number, payload: BicicletaAdminDTO): Observable<BicicletaAdminDTO> {
    return this.http.put<BicicletaAdminDTO>(`${this.base}/${id}`, payload);
  }
  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.base}/${id}`);
  }

  // ⛳️ este endpoint actualiza la imagen de una bici existente
  actualizarImagenBicicleta(id: number, file: File): Observable<{ imagenUrl: string }> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.put<{ imagenUrl: string }>(`${this.base}/${id}/imagen`, fd);
  }
}
