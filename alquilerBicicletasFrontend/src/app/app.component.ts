import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ProcesoReservaComponent} from './components/reserva/proceso-reserva/proceso-reserva.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'alquilerBicicletasFronted';
}
