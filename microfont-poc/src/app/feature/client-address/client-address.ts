import {
  Component,
  Input,
  output,
  signal,
  WritableSignal,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import { InputSelectOptionField } from '../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { Option } from '../../shared/common-components/generic-component-type/generic-data-grid/generic-data-grid';
import { InputNumber } from '../../shared/common-components/input-types/input-number/input-number';
import { InputTextArea } from '../../shared/common-components/input-types/input-text-area/input-text-area';

@Component({
  selector: 'app-client-address',
  imports: [
    ExpansionPanelHeader,
    InputTextBox,
    InputSelectOptionField,
    InputNumber,
    InputTextArea,
  ],
  templateUrl: './client-address.html',
  styleUrl: './client-address.scss',
})
export class ClientAddress {
  @Input({ required: true })
  group!: FormGroup<any>;
  addressHeaderPanel: WritableSignal<boolean> = signal(true);

  readonly countrySelected = output<string>();
  readonly divisionSelected = output<string>();
  readonly districtSelected = output<string>();

  @Input({
    required: true,
  })
  countryList: {
    countryName: string;
    id: number;
  }[] = [];

  @Input({
    required: true,
  })
  divisionList: {
    divisionName: string;
    id: number;
  }[] = [];

  @Input({
    required: true,
  })
  districtList: {
    districtName: string;
    id: number;
  }[] = [];

  @Input({
    required: true,
  })
  thanaList: {
    thanaName: string;
    id: number;
  }[] = [];

  @Input({
    required: true,
  })
  addressTypeList: {
    addressTypeName: string;
    id: number;
  }[] = [];

  get countryOptions(): Option[] {
    return this.countryList.map((country) => ({
      key: country.countryName,
      value: country.countryName,
    }));
  }

  get addressTypeOptions(): Option[] {
    return this.addressTypeList.map((type) => ({
      key: type.addressTypeName,
      value: type.addressTypeName,
    }));
  }

  get divisionOptions(): Option[] {
    return this.divisionList.map((division) => ({
      key: division.divisionName,
      value: division.divisionName,
    }));
  }

  get districtOptions(): Option[] {
    return this.districtList.map((district) => ({
      key: district.districtName,
      value: district.districtName,
    }));
  }

  get thanaOptions(): Option[] {
    return this.thanaList.map((thana) => ({
      key: thana.thanaName,
      value: thana.thanaName,
    }));
  }

  onCountrySelected(selectedValue: string): void {
    this.countrySelected.emit(selectedValue);
  }

  onDivisionSelected(selectedValue: string): void {
    this.divisionSelected.emit(selectedValue);
  }

  onDistrictSelected(selectedValue: string): void {
    this.districtSelected.emit(selectedValue);
  }
}
