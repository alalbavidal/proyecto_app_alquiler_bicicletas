import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AccesoriosAdminService } from '../accesorios-admin.service';
import { AccesorioAdminDTO } from '../accesorio-admin.dto';

@Component({
  standalone: true,
  selector: 'app-accesorio-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './accesorio-form.component.html',
  styleUrls: ['./accesorio-form.component.css']
})
export class AccesorioFormComponent implements OnInit {
  private fb = inject(FormBuilder);

  id?: number;
  loading = false;
  saving = false;
  error = '';

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: [''],
    precioDia: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    observaciones: [''],
    activo: [true, Validators.required],
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private srv: AccesoriosAdminService
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.id = paramId ? +paramId : undefined;

    if (this.id) {
      this.loading = true;
      this.srv.obtener(this.id).subscribe({
        next: (a) => { this.form.patchValue(a); this.loading = false; },
        error: (e) => { this.error = e?.error || 'No se pudo cargar el accesorio'; this.loading = false; }
      });
    }
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = '';

    // Asegura números
    const raw = this.form.getRawValue();
    const payload: AccesorioAdminDTO = {
      ...raw,
      precioDia: Number(raw.precioDia ?? 0),
      stock: Number(raw.stock ?? 0),
    } as AccesorioAdminDTO;

    const obs = this.id ? this.srv.actualizar(this.id, payload) : this.srv.crear(payload);
    obs.subscribe({
      next: () => { this.saving = false; this.router.navigate(['/admin/accesorios']); },
      error: (e) => { this.saving = false; this.error = e?.error || 'No se pudo guardar'; }
    });
  }

  cancelar() { this.router.navigate(['/admin/accesorios']); }
}
