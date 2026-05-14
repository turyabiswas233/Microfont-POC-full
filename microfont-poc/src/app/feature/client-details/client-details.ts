import { Component, Input, signal, WritableSignal } from '@angular/core';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import { FormGroup } from '@angular/forms';
import { InputDate } from '../../shared/common-components/input-types/input-date/input-date';
import { InputSelectOptionField } from '../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { Option } from '../../shared/common-components/generic-component-type/generic-data-grid/generic-data-grid';
import { InputNumber } from '../../shared/common-components/input-types/input-number/input-number';

@Component({
  selector: 'app-client-details',
  imports: [
    ExpansionPanelHeader,
    InputTextBox,
    InputDate,
    InputSelectOptionField,
    InputNumber,
  ],
  templateUrl: './client-details.html',
  styleUrl: './client-details.scss',
})
export class ClientDetails {
  @Input({ required: true })
  group!: FormGroup<any>;

  detailsHeaderPanel: WritableSignal<boolean> = signal(true);
  genderList: Option[] = [
    { key: 'Male', value: 'male' },
    { key: 'Female', value: 'female' },
  ];
  maritalStatusList: Option[] = [
    {
      key: 'Single',
      value: 'single',
    },
    {
      key: 'Married',
      value: 'married',
    },
    {
      key: 'Divorced',
      value: 'divorced',
    },
    {
      key: 'Widowed',
      value: 'widowed',
    },
  ];
}
