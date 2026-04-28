import { Component, EventEmitter, Output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Cliente } from '../../../models/cliente.model';
import {UploadsService} from '../../../services/upload.service';

// ⬅️ OJO: este es el servicio nuevo centralizado

type TipoDocumento = Cliente['tipoDocumento'];

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css']
})
export class ClienteFormComponent implements OnDestroy {
  @Output() clienteConfirmado = new EventEmitter<Cliente>();

  cliente: Cliente = {
    nombre: '',
    apellido: '',
    email: '',
    fechaNacimiento: '',
    telefono: '',
    documentoIdentidad: '',
    tipoDocumento: 'DNI',
    fotoDocumentoUrl: ''
  };

  uploading = false;
  previewUrl: string | null = null;
  uploadError = '';

  // ⬅️ Alineado con el backend: solo jpeg/png/webp (no GIF)
  readonly allowedTypes = ['image/jpeg', 'image/png', 'image/webp'] as const;
  readonly maxSize = 5 * 1024 * 1024; // 5MB

  // Validaciones suaves por tipo de documento
  private patterns: Record<TipoDocumento, RegExp> = {
    DNI: /^[0-9]{7,8}[A-Za-z]$/,
    NIE: /^[XYZxyz][0-9]{7}[A-Za-z]$/,
    PASAPORTE: /^[A-Za-z0-9]{5,20}$/,
    OTRO: /^[A-Za-z0-9\-_/\.]{4,30}$/
  };

  constructor(private uploads: UploadsService) {}

  ngOnDestroy(): void {
    if (this.previewUrl?.startsWith('blob:')) {
      URL.revokeObjectURL(this.previewUrl);
    }
  }

  // Valida el documento con el patrón del tipo elegido
  isDocumentoValido(): boolean {
    const tipo = this.cliente.tipoDocumento as TipoDocumento;
    const valor = (this.cliente.documentoIdentidad || '').trim().toUpperCase();
    return !!valor && this.patterns[tipo].test(valor);
  }

  onSubmit(form?: NgForm): void {
    if (
      !this.cliente.nombre?.trim() ||
      !this.cliente.apellido?.trim() ||
      !this.cliente.email?.trim() ||
      !this.cliente.fechaNacimiento ||
      !this.isDocumentoValido()
    ) {
      // aquí puedes disparar un toast desde el padre si quieres
      return;
    }

    // Normaliza el documento a MAYÚSCULAS
    this.cliente.documentoIdentidad = this.cliente.documentoIdentidad.trim().toUpperCase();

    this.clienteConfirmado.emit(this.cliente);
  }

  onFileSelected(evt: Event): void {
    const input = evt.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    if (!this.allowedTypes.includes(file.type as any)) {
      this.uploadError = 'Formato no permitido. Usa JPG, PNG o WEBP.';
      return;
    }
    if (file.size > this.maxSize) {
      this.uploadError = 'El archivo supera 5MB.';
      return;
    }

    this.uploading = true;
    this.previewUrl = URL.createObjectURL(file);
    this.uploadError = '';

    this.uploads.subirDocumento(file).subscribe({
      next: ({ url }) => {
        this.cliente.fotoDocumentoUrl = url; // URL pública servida por backend
        this.uploading = false;
      },
      error: (e) => {
        this.uploading = false;
        this.uploadError = e?.error?.error || 'Error subiendo el documento';
      }
    });
  }
}
