// src/app/services/uploads.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Si usas proxy, puedes dejar base = '/api/uploads'.
// Si no, usa 'http://localhost:8085/api/uploads'.
const BASE = '/api/uploads';

export interface UploadResponse {
  url: string;
  filename: string;
}

@Injectable({ providedIn: 'root' })
export class UploadsService {
  constructor(private http: HttpClient) {}

  subirDocumento(file: File): Observable<UploadResponse> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<UploadResponse>(`${BASE}/documentos`, fd);
    // -> backend responde { url, filename }
  }

  subirImagenBicicleta(file: File): Observable<UploadResponse> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<UploadResponse>(`${BASE}/bicicletas`, fd);
  }
}
