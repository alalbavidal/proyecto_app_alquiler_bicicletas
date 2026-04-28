// src/app/adm/bicicletas/bicicleta-form/bicicleta-form.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BicicletasAdminService } from '../bicicletas-admin.service';
import { BicicletaAdminDTO } from '../bicicleta-admin.dto';
import { EstadoBicicleta } from '../estado-bicicleta.enum';
import {UploadsService} from '../../../services/upload.service';

@Component({
  standalone: true,
  selector: 'app-bicicleta-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bicicleta-form.component.html',
  styleUrls: ['./bicicleta-form.component.css']
})
export class BicicletaFormComponent implements OnInit {
  private fb = inject(FormBuilder);

  id?: number;
  estados = Object.values(EstadoBicicleta);
  loading = false;
  saving = false;
  error = '';

  // Subida de imagen
  uploading = false;
  uploadError = '';
  previewSrc?: string;
  // ⬇️ Alineado con backend (no GIF)
  readonly allowedTypes = ['image/png','image/jpeg','image/webp'];
  readonly maxSize = 5 * 1024 * 1024; // 5MB

  form = this.fb.group({
    modelo: ['', [Validators.required, Validators.maxLength(50)]],
    numero: ['', [Validators.required, Validators.maxLength(10)]],
    bastidor: [''],
    candado: [''],
    claveCandado: [''],
    descripcion: [''],
    imagenUrl: [''],
    estado: [EstadoBicicleta.ACTIVO, Validators.required],
    observaciones: [''],
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private srv: BicicletasAdminService,
    private uploads: UploadsService
  ) {}

  ngOnInit() {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.id = paramId ? +paramId : undefined;

    if (this.id) {
      this.loading = true;
      this.srv.obtener(this.id).subscribe({
        next: (b) => {
          this.form.patchValue(b);
          this.previewSrc = b.imagenUrl || undefined;
          this.loading = false;
        },
        error: (e) => { this.error = e?.error || 'No se pudo cargar la bicicleta'; this.loading = false; }
      });
    }
  }

  onPickFile(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    if (!this.allowedTypes.includes(file.type)) {
      this.uploadError = 'Formato no permitido. Usa JPG, PNG o WEBP.';
      return;
    }
    if (file.size > this.maxSize) {
      this.uploadError = 'El archivo supera 5MB.';
      return;
    }

    this.previewSrc = URL.createObjectURL(file);
    this.uploadError = '';

    // Si estoy editando → PUT que persiste imagenUrl
    if (this.id) {
      this.uploading = true;
      this.srv.actualizarImagenBicicleta(this.id, file).subscribe({
        next: ({ imagenUrl }) => {
          this.form.patchValue({ imagenUrl });
          this.previewSrc = imagenUrl || this.previewSrc;
          this.uploading = false;
        },
        error: (e) => {
          this.uploading = false;
          this.uploadError = e?.error?.error || 'Error subiendo/asignando la imagen';
        }
      });
      return;
    }

    // Si estoy creando → primero subo para obtener la URL y guardarla en el form
    this.uploading = true;
    this.uploads.subirImagenBicicleta(file).subscribe({
      next: ({ url }) => {
        this.form.patchValue({ imagenUrl: url });
        this.uploading = false;
      },
      error: (e) => {
        this.uploading = false;
        this.uploadError = e?.error?.error || 'Error subiendo la imagen';
      }
    });
  }

  limpiarImagen() {
    if (this.previewSrc?.startsWith('blob:')) URL.revokeObjectURL(this.previewSrc);
    this.previewSrc = undefined;
    this.form.patchValue({ imagenUrl: '' });
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = '';
    const payload: BicicletaAdminDTO = this.form.getRawValue() as BicicletaAdminDTO;
    const obs = this.id ? this.srv.actualizar(this.id, payload) : this.srv.crear(payload);
    obs.subscribe({
      next: () => { this.saving = false; this.router.navigate(['/admin/bicicletas']); },
      error: (e) => { this.saving = false; this.error = e?.error || 'No se pudo guardar'; }
    });
  }

  cancelar() { this.router.navigate(['/admin/bicicletas']); }
}
