import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
})
export class ToastComponent {
  @Input() type: ToastType = 'info';
  @Input() title = '';
  @Input() message = '';
  @Input() actionLabel = '';
  @Input() duration = 4000;          // 0 = no auto-cierre
  @Input() backdrop = true;          // fondo semitransparente
  @Input() centered = true;          // centrado en pantalla

  @Output() action = new EventEmitter<void>();

  visible = signal(false);
  private timer: any;

  open(opts: {
    type?: ToastType;
    title?: string;
    message: string;
    actionLabel?: string;
    duration?: number;
    backdrop?: boolean;
    centered?: boolean;
  }) {
    if (this.timer) clearTimeout(this.timer);

    this.type = opts.type ?? this.type;
    this.title = opts.title ?? '';
    this.message = opts.message;
    this.actionLabel = opts.actionLabel ?? '';
    this.duration = typeof opts.duration === 'number' ? opts.duration : this.duration;
    this.backdrop = typeof opts.backdrop === 'boolean' ? opts.backdrop : this.backdrop;
    this.centered = typeof opts.centered === 'boolean' ? opts.centered : this.centered;

    this.visible.set(true);
    if (this.duration > 0) {
      this.timer = setTimeout(() => this.close(), this.duration);
    }
  }

  close() {
    this.visible.set(false);
    if (this.timer) clearTimeout(this.timer);
  }

  onActionClick() {
    this.action.emit();
    this.close();
  }

  iconClass() {
    switch (this.type) {
      case 'success': return 'bi-check-circle-fill';
      case 'error':   return 'bi-x-circle-fill';
      case 'warning': return 'bi-exclamation-triangle-fill';
      default:        return 'bi-info-circle-fill';
    }
  }
}
