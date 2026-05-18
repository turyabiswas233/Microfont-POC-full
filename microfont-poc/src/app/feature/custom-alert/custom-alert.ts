import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

export type AlertVariant = 'success' | 'info' | 'error';

@Component({
  selector: 'app-custom-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './custom-alert.html',
  styleUrl: './custom-alert.scss',
})
export class CustomAlert {
  readonly isOpen = input<boolean>(false);
  readonly title = input<string>('Success!');
  readonly message = input<string>('');
  readonly variant = input<AlertVariant>('success');
  readonly showBackdrop = input<boolean>(true);
  readonly buttonText = input<string>('OK');

  readonly close = output<void>();
  readonly buttonClick = output<{ action: string }>();

  get iconPath(): string {
    switch (this.variant()) {
      case 'info':
        return 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
      case 'error':
        return 'M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
      case 'success':
      default:
        return 'M5 13l4 4L19 7';
    }
  }

  get iconContainerClass(): string {
    switch (this.variant()) {
      case 'info':
        return 'bg-blue-50 border-blue-200';
      case 'error':
        return 'bg-red-50 border-red-200';
      case 'success':
      default:
        return 'bg-green-50 border-green-200';
    }
  }

  get iconClass(): string {
    switch (this.variant()) {
      case 'info':
        return 'text-blue-600';
      case 'error':
        return 'text-red-600';
      case 'success':
      default:
        return 'text-green-600';
    }
  }

  get buttonClass(): string {
    switch (this.variant()) {
      case 'info':
        return 'border-transparent text-white bg-blue-600 hover:bg-blue-700 focus:ring-blue-500';
      case 'error':
        return 'border-transparent text-white bg-red-600 hover:bg-red-700 focus:ring-red-500';
      case 'success':
      default:
        return 'border-transparent text-white bg-green-600 hover:bg-green-700 focus:ring-green-500';
    }
  }

  onClose(): void {
    this.close.emit();
  }

  onButtonClick(): void {
    this.buttonClick.emit({ action: 'ok' });
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget && this.showBackdrop()) {
      this.onClose();
    }
  }
}
