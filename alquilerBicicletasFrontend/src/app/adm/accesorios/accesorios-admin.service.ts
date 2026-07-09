import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {Observable, map, switchMap} from 'rxjs';
import { AccesorioAdminDTO } from './accesorio-admin.dto';

@Injectable({ providedIn: 'root' })
export class AccesoriosAdminService {
  private base = 'http://localhost:8085/api/accesorios';

  constructor(private http: HttpClient) {}

  // Para admin queremos ver TODOS (activos e inactivos)
  listarTodos(): Observable<AccesorioAdminDTO[]> {
    // /filtrar sin params = devuelve todos
    return this.http.get<AccesorioAdminDTO[]>(`${this.base}/filtrar`);
  }

  listarActivos(): Observable<AccesorioAdminDTO[]> {
    const params = new HttpParams().set('activo', true);
    return this.http.get<AccesorioAdminDTO[]>(`${this.base}/filtrar`, { params });
  }

  obtener(id: number): Observable<AccesorioAdminDTO> {
    return this.http.get<AccesorioAdminDTO>(`${this.base}/${id}`);
  }

  crear(payload: AccesorioAdminDTO): Observable<AccesorioAdminDTO> {
    return this.http.post<AccesorioAdminDTO>(this.base, payload);
  }

  actualizar(id: number, payload: AccesorioAdminDTO): Observable<AccesorioAdminDTO> {
    return this.http.put<AccesorioAdminDTO>(`${this.base}/${id}`, payload);
  }

  // Borrado lógico (activa=false)
  desactivar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  // Restaurar (activa=true)
  restaurar(id: number): Observable<AccesorioAdminDTO> {
    return this.obtener(id).pipe(
      switchMap(a => this.actualizar(id, { ...a, activo: true }))
    );
  }

  restaurarDirecto(id: number): Observable<AccesorioAdminDTO> {
    // helper: obtiene y hace PUT con activo=true
    return this.obtener(id).pipe(
      map(a => ({ ...a, activo: true } as AccesorioAdminDTO)),
      // @ts-ignore-next-line – silencioso si da guerra por el genérico
      // pero mejor explícito:
    ) as unknown as Observable<AccesorioAdminDTO>;
  }






}
