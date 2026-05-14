import { CommonModule } from '@angular/common';
import {
  Component,
  Input,
  Output,
  EventEmitter,
  signal,
  WritableSignal,
} from '@angular/core';

import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { GenericButton } from '../../shared/common-components/generic-component-type/generic-button/generic-button';
import { InputIdBox } from '../../shared/common-components/input-types/input-id-box/input-id-box';

@Component({
  selector: 'app-client-info',
  imports: [
    CommonModule,
    InputTextBox,
    FormsModule,
    ReactiveFormsModule,
    ExpansionPanelHeader,
    GenericButton,
    InputIdBox,
  ],
  templateUrl: './client-info.html',
  styleUrl: './client-info.scss',
})
export class ClientInfo {
  @Input({ required: true })
  group!: FormGroup<any>;
  @Input() isEditMode: boolean = false;

  @Output() genIdClicked = new EventEmitter<void>();
  infoHeaderPanel: WritableSignal<boolean> = signal(true);

  onGenIdClick(): void {
    this.genIdClicked.emit();
  }
}
