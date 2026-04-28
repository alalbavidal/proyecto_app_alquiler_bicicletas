// src/app/adm/tarifas/tarifas-list/tarifas-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router } from '@angular/router';
import { TarifasAdminService } from '../tarifas-admin.service';
import { TarifaAdminDTO } from '../tarifa-admin.dto';

@Component({
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  selector: 'app-tarifas-list',
  templateUrl: './tarifas-list.component.html',
  styleUrls: ['./tarifas-list.component.css'],
})
export class TarifasListComponent implements OnInit {
  tarifas: TarifaAdminDTO[] = [];
  loading = false;
  error = '';

  constructor(private srv: TarifasAdminService, private router: Router) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.srv.listar().subscribe({
      next: (data) => { this.tarifas = data; this.loading = false; },
      error: (e) => { this.error = e?.error || 'No se pudieron cargar las tarifas'; this.loading = false; }
    });
  }

  crear() { this.router.navigate(['/admin/tarifas/new']); }
  editar(t: TarifaAdminDTO) { this.router.navigate(['/admin/tarifas', t.id, 'edit']); }

  toggleActiva(t: TarifaAdminDTO) {
    if (!t.id) return;
    this.srv.setActiva(t.id, !t.activa).subscribe({
      next: (res) => { t.activa = res.activa; },
      error: (e) => { this.error = e?.error || 'No se pudo cambiar el estado'; }
    });
  }

  borrar(t: TarifaAdminDTO) {
    if (!t.id) return;
    if (!confirm(`¿Eliminar tarifa "${t.nombre}"?`)) return;
    this.srv.eliminar(t.id).subscribe({
      next: () => this.load(),
      error: (e) => { this.error = e?.error || 'No se pudo eliminar'; }
    });
  }
}
