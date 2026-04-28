import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router } from '@angular/router';
import { AccesoriosAdminService } from '../accesorios-admin.service';
import { AccesorioAdminDTO } from '../accesorio-admin.dto';

@Component({
  standalone: true,
  selector: 'app-accesorios-list',
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './accesorios-list.component.html',
  styleUrls: ['./accesorios-list.component.css']
})
export class AccesoriosListComponent implements OnInit {
  loading = false;
  error = '';
  accesorios: AccesorioAdminDTO[] = [];
  mostrarSoloActivos = false;

  constructor(private srv: AccesoriosAdminService, private router: Router) {}

  ngOnInit(): void { this.cargar(); }

  cargar() {
    this.loading = true;
    const obs = this.mostrarSoloActivos ? this.srv.listarActivos() : this.srv.listarTodos();
    obs.subscribe({
      next: data => { this.accesorios = data; this.loading = false; },
      error: e => { this.error = e?.error || 'No se pudieron cargar los accesorios'; this.loading = false; }
    });
  }

  toggleFiltroActivos() {
    this.mostrarSoloActivos = !this.mostrarSoloActivos;
    this.cargar();
  }

  crear() { this.router.navigate(['/admin/accesorios/new']); }
  editar(a: AccesorioAdminDTO) { this.router.navigate(['/admin/accesorios', a.id, 'edit']); }

  desactivar(a: AccesorioAdminDTO) {
    if (!a.id) return;
    this.srv.desactivar(a.id).subscribe({
      next: () => this.cargar(),
      error: e => this.error = e?.error || 'No se pudo desactivar'
    });
  }

  restaurar(a: AccesorioAdminDTO) {
    if (!a.id) return;
    this.srv.restaurar(a.id).subscribe({
      next: () => this.cargar(),
      error: e => this.error = e?.error || 'No se pudo restaurar'
    });
  }
}
