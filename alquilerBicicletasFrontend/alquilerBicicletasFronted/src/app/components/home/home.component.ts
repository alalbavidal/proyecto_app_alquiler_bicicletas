import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  constructor(private router: Router) {}

  irAlCalendario() {
    this.router.navigate(['/calendario']);
  }

  // En home.component.ts
showScrollButton: boolean = false;

ngOnInit() {
  // Detectar scroll para mostrar/ocultar el botón
  window.addEventListener('scroll', () => {
    this.showScrollButton = window.scrollY > 300;
  });
}

scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}
}

