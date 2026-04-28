import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { BicicletasAdminService } from '../bicicletas-admin.service';
import { BicicletaAdminDTO } from '../bicicleta-admin.dto';

@Component({
  standalone: true,
  selector: 'app-bicicletas-list',
  imports: [CommonModule],
  templateUrl: './bicicletas-list.component.html',
  styleUrls: ['./bicicletas-list.component.css']
})
export class BicicletasListComponent implements OnInit {
  loading = false;
  data: BicicletaAdminDTO[] = [];
  error = '';

  constructor(private srv: BicicletasAdminService, private router: Router) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.loading = true; this.error = '';
    this.srv.listar().subscribe({
      next: (rows: BicicletaAdminDTO[]) => { this.data = rows; this.loading = false; },
      error: (e: { error: string; }) => { this.error = e?.error || 'Error cargando bicicletas'; this.loading = false; }
    });
  }

  crear() { this.router.navigate(['/admin/bicicletas/new']); }
  editar(id?: number) { if (id) this.router.navigate(['/admin/bicicletas', id, 'edit']); }
  eliminar(b: BicicletaAdminDTO) {
    if (!b.id) return;
    if (!confirm(`¿Eliminar la bicicleta ${b.modelo}?`)) return;
    this.srv.eliminar(b.id).subscribe({ next: () => this.cargar() });
  }
}
