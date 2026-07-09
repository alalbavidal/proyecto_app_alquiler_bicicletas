// src/app/adm/tarifas/tarifa-form/tarifa-form.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TarifasAdminService } from '../tarifas-admin.service';
import { TarifaAdminDTO } from '../tarifa-admin.dto';

@Component({
  standalone: true,
  selector: 'app-tarifa-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './tarifa-form.component.html',
  styleUrls: ['./tarifa-form.component.css']
})
export class TarifaFormComponent implements OnInit {
  private fb = inject(FormBuilder);

  id?: number;
  loading = false;
  saving = false;
  error = '';

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(50)]],
    descripcion: ['', [Validators.required, Validators.maxLength(100)]],
    precio: [null as number | null, [Validators.required, Validators.min(0.01)]],
    precioPorDia: [false, [Validators.required]],
    horasIncluidas: [null as number | null],
    diasMinimos: [null as number | null],
    horaInicio: [null as string | null], // "HH:mm:ss"
    horaFin: [null as string | null],
    activa: [true, [Validators.required]],
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private srv: TarifasAdminService
  ) {}

  ngOnInit() {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.id = paramId ? +paramId : undefined;

    // Reglas dinámicas
    this.form.get('precioPorDia')!.valueChanges.subscribe((v) => this.applyModeValidators(!!v));
    this.applyModeValidators(this.form.get('precioPorDia')!.value!);

    if (this.id) {
      this.loading = true;
      this.srv.obtener(this.id).subscribe({
        next: (t) => {
          // normaliza horaInicio/horaFin -> "HH:mm:ss"
          const patch: TarifaAdminDTO = {
            ...t,
            horaInicio: t.horaInicio || null,
            horaFin: t.horaFin || null
          };
          this.form.patchValue(patch);
          this.loading = false;
        },
        error: (e) => { this.error = e?.error || 'No se pudo cargar la tarifa'; this.loading = false; }
      });
    }
  }

  private applyModeValidators(porDia: boolean) {
    const horasIncluidas = this.form.get('horasIncluidas')!;
    const diasMinimos = this.form.get('diasMinimos')!;
    const horaInicio = this.form.get('horaInicio')!;
    const horaFin = this.form.get('horaFin')!;

    // limpia
    horasIncluidas.clearValidators();
    diasMinimos.clearValidators();
    horaInicio.clearValidators();
    horaFin.clearValidators();

    if (porDia) {
      // por días -> exigir díasMinimos >=1
      diasMinimos.setValidators([Validators.required, Validators.min(1)]);
      // horario opcional
    } else {
      // por horas -> exigir horasIncluidas >=1 O un rango (dejamos ambos opcionales aquí y validamos en backend)
      horasIncluidas.setValidators([Validators.min(1)]);
      // si quieres obligar un rango, añade required a ambos:
      // horaInicio.setValidators([Validators.required]);
      // horaFin.setValidators([Validators.required]);
    }

    horasIncluidas.updateValueAndValidity();
    diasMinimos.updateValueAndValidity();
    horaInicio.updateValueAndValidity();
    horaFin.updateValueAndValidity();
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = '';
    const payload = this.form.getRawValue() as TarifaAdminDTO;

    const obs = this.id ? this.srv.actualizar(this.id, payload) : this.srv.crear(payload);
    obs.subscribe({
      next: () => { this.saving = false; this.router.navigate(['/admin/tarifas']); },
      error: (e) => { this.saving = false; this.error = e?.error || 'No se pudo guardar'; }
    });
  }

  cancelar() { this.router.navigate(['/admin/tarifas']); }
}
