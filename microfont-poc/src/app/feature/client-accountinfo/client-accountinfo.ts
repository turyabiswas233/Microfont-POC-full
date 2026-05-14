import { Component, Input, signal, WritableSignal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import { InputNumber } from '../../shared/common-components/input-types/input-number/input-number';
import { InputDate } from '../../shared/common-components/input-types/input-date/input-date';

@Component({
  selector: 'app-client-accountinfo',
  imports: [ExpansionPanelHeader, InputTextBox, InputNumber, InputDate],
  templateUrl: './client-accountinfo.html',
  styleUrl: './client-accountinfo.scss',
})
export class ClientAccountinfo {
  accountInfoHeaderPanel: WritableSignal<boolean> = signal(true);
  @Input({ required: true })
  group!: FormGroup<any>;
}
