import {
  Component,
  inject,
  numberAttribute,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { Router } from '@angular/router';
import { ClientAddressService } from '../client-registration/service/client-address.service';
import { AddressService as AddressLookupService } from '../client-registration/service/address.service';
import { ClientAddress } from '../client-address/client-address';
import { GenericDataGrid } from '../../shared/common-components/generic-component-type/generic-data-grid';
import { GenericModal } from '../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import { GenericButton } from '../../shared/common-components/generic-component-type/generic-button/generic-button';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { firstValueFrom, forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AlertVariant, CustomAlert } from '../custom-alert/custom-alert';
import { InputTextArea } from '../../shared/common-components/input-types/input-text-area/input-text-area';
import { ConfirmationDialogue } from '../../shared/common-components/confirmation-dialogue/confirmation-dialogue';

type AddressGridRow = {
  retrieveClientInfo: {
    clientId: number;
    clientName: string;
  };
  retrieveClientAddress: {
    addressType: string;
    country: string | number;
    division: string | number;
    district: string | number;
    thana: string | number;
    city: string;
    zipCode: string;
    mobileNumber: string;
    email: string;
    address: string;
  };
};

const ADDRESS_GRID_FIELDS = {
  'retrieveClientInfo.clientId': 'Client ID',
  'retrieveClientInfo.clientName': 'Client Name',
  'retrieveClientAddress.addressType': 'Address Type',
  'retrieveClientAddress.address': 'Address',
  'retrieveClientAddress.country': 'Country',
  'retrieveClientAddress.division': 'Division',
  'retrieveClientAddress.district': 'District',
  'retrieveClientAddress.thana': 'Thana',
  'retrieveClientAddress.city': 'City',
  'retrieveClientAddress.zipCode': 'Zip Code',
  'retrieveClientAddress.mobileNumber': 'Mobile No',
  'retrieveClientAddress.email': 'Email',
} as const;

@Component({
  selector: 'app-all-address',
  imports: [
    ClientAddress,
    GenericButton,
    GenericDataGrid,
    GenericModal,
    ExpansionPanelHeader,
    InputTextBox,
    CustomAlert,
    InputTextArea,
    ConfirmationDialogue,
  ],
  templateUrl: './all-address.html',
  styleUrl: './all-address.scss',
})
export class AllAddress implements OnInit {
  clientAddresses: AddressGridRow[] = [];
  selectedAddress: AddressGridRow | null = null;
  editAddressRow: AddressGridRow | null = null;
  infoHeaderPanel: WritableSignal<boolean> = signal(false);
  editInfoHeaderPanel: WritableSignal<boolean> = signal(false);
  addressDetailsGroup: FormGroup;
  editAddressGroup: FormGroup;
  editErrorMessage: string | null = null;
  isSavingEdit = false;
  columns = Object.keys(
    ADDRESS_GRID_FIELDS,
  ) as (keyof typeof ADDRESS_GRID_FIELDS)[];
  columnLabels = ADDRESS_GRID_FIELDS;

  addressTypes: {
    id: number;
    addressTypeName: string;
  }[] = [];
  countries: {
    countryName: string;
    id: number;
  }[] = [];
  divisions: {
    divisionName: string;
    id: number;
  }[] = [];
  districts: {
    districtName: string;
    id: number;
  }[] = [];
  thanas: {
    thanaName: string;
    id: number;
  }[] = [];

  clientService = inject(ClientAddressService);
  addressLookupService = inject(AddressLookupService);
  router = inject(Router);

  isSuccessModalOpen = signal(false);
  successModalTitle = signal('Success!');
  successModalMessage = signal('');
  alertVarient: AlertVariant = 'success';
  isDeleteConfirmationOpen = signal(false);
  deleteMessage = signal('');
  pendingDeleteClientId: number | null = null;

  ngOnInit(): void {
    this.loadClientAddresses();
    this.loadAddressTypes();
    this.loadCountries();
    this.addressDetailsGroup = new FormGroup({
      clientId: new FormControl('', { nonNullable: true }),
      clientName: new FormControl('', { nonNullable: true }),
      addressType: new FormControl('', { nonNullable: true }),
      country: new FormControl('', { nonNullable: true }),
      division: new FormControl('', { nonNullable: true }),
      district: new FormControl('', { nonNullable: true }),
      thana: new FormControl('', { nonNullable: true }),
      city: new FormControl('', { nonNullable: true }),
      zipCode: new FormControl('', { nonNullable: true }),
      mobileNumber: new FormControl('', { nonNullable: true }),
      email: new FormControl('', { nonNullable: true }),
      address: new FormControl('', { nonNullable: true }),
    });
    this.editAddressGroup = new FormGroup({
      clientId: new FormControl('', { nonNullable: true }),
      clientName: new FormControl('', { nonNullable: true }),
      addressType: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      country: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      division: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      district: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      thana: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      city: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      zipCode: new FormControl('', { nonNullable: true }),
      mobileNumber: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      email: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.email],
      }),
      address: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
    });
    console.log('AllAddress component initialized', this.clientAddresses);
  }

  loadClientAddresses(): void {
    this.clientService.getClientAddresses().subscribe((addresses) => {
      this.clientAddresses = addresses;
    });
  }

  handleDelete(event: string): void {
    const clientId = this.parseGridEvent(event)?.retrieveClientInfo.clientId;
    console.log('Delete address for client ID:', clientId);
    if (clientId !== undefined && clientId !== null) {
      this.openDeleteConfirmation(clientId);
    }
  }

  handleModalEdit(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }
    this.selectedAddress = null;
    this.editAddressRow = row;
    this.editErrorMessage = null;

    this.editAddressGroup.patchValue({
      clientId: row.retrieveClientInfo.clientId,
      clientName: row.retrieveClientInfo.clientName,
      addressType: row.retrieveClientAddress.addressType,
      country: row.retrieveClientAddress.country,
      division: row.retrieveClientAddress.division,
      district: row.retrieveClientAddress.district,
      thana: row.retrieveClientAddress.thana,
      city: row.retrieveClientAddress.city,
      zipCode: row.retrieveClientAddress.zipCode,
      mobileNumber: row.retrieveClientAddress.mobileNumber,
      email: row.retrieveClientAddress.email,
      address: row.retrieveClientAddress.address,
    });

    void this.loadEditAddressLookups(row.retrieveClientAddress);
  }

  handleModalView(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }

    this.editAddressRow = null;
    this.selectedAddress = row;
    this.infoHeaderPanel.set(true);

    this.addressDetailsGroup.patchValue({
      clientId: row.retrieveClientInfo.clientId,
      clientName: row.retrieveClientInfo.clientName,
      addressType: row.retrieveClientAddress.addressType,
      country: row.retrieveClientAddress.country,
      division: row.retrieveClientAddress.division,
      district: row.retrieveClientAddress.district,
      thana: row.retrieveClientAddress.thana,
      city: row.retrieveClientAddress.city,
      zipCode: row.retrieveClientAddress.zipCode,
      mobileNumber: row.retrieveClientAddress.mobileNumber,
      email: row.retrieveClientAddress.email,
      address: row.retrieveClientAddress.address,
    });

    this.resolveLocationNames(row.retrieveClientAddress);
  }

  onModalClosed(): void {
    this.selectedAddress = null;
  }

  onEditModalClosed(): void {
    this.editAddressRow = null;
    this.editErrorMessage = null;
    this.editAddressGroup.reset();
  }

  closeDeleteConfirmation(): void {
    this.isDeleteConfirmationOpen.set(false);
    this.deleteMessage.set('');
    this.pendingDeleteClientId = null;
  }

  onDeleteConfirmationButtonClick(event: {
    action: string;
    button: { action?: string };
  }): void {
    if (event.action === 'confirm') {
      const clientId = this.pendingDeleteClientId;
      this.closeDeleteConfirmation();
      if (clientId === null) {
        return;
      }
      this.clientService
        .deleteClientAddress(numberAttribute(clientId))
        .subscribe((isDeleted) => {
          if (isDeleted) {
            this.alertVarient = 'success';
            this.openSuccessModal('Address deleted successfully.');
            this.loadClientAddresses();
          } else {
            this.alertVarient = 'error';
            this.openSuccessModal(
              'Failed to delete address. Please try again.',
            );
          }
        });
    } else {
      this.closeDeleteConfirmation();
    }
  }

  onEditCountrySelected(countryName: string): void {
    this.divisions = [];
    this.districts = [];
    this.thanas = [];
    this.editAddressGroup.patchValue({
      division: '',
      district: '',
      thana: '',
    });

    if (typeof countryName === 'string' && countryName.trim()) {
      this.loadDivisions(countryName);
    }
  }

  onEditDivisionSelected(divisionName: string): void {
    this.districts = [];
    this.thanas = [];
    this.editAddressGroup.patchValue({
      district: '',
      thana: '',
    });

    if (typeof divisionName === 'string' && divisionName.trim()) {
      this.loadDistricts(divisionName);
    }
  }

  onEditDistrictSelected(districtName: string): void {
    this.thanas = [];
    this.editAddressGroup.patchValue({
      thana: '',
    });

    if (typeof districtName === 'string' && districtName.trim()) {
      this.loadThanas(districtName);
    }
  }

  saveEdit(): void {
    if (this.editAddressGroup.invalid) {
      this.editAddressGroup.markAllAsTouched();
      return;
    }

    const clientId = Number(this.editAddressGroup.get('clientId')?.value);
    if (!Number.isFinite(clientId)) {
      this.alertVarient = 'error';
      this.openSuccessModal('Client ID is missing.');
      return;
    }

    const formValue = this.editAddressGroup.getRawValue();
    const payload = {
      addressType: this.lookupId(
        this.addressTypes,
        'addressTypeName',
        formValue.addressType,
      ),
      country: this.lookupId(this.countries, 'countryName', formValue.country),
      division: this.lookupId(
        this.divisions,
        'divisionName',
        formValue.division,
      ),
      district: this.lookupId(
        this.districts,
        'districtName',
        formValue.district,
      ),
      thana: this.lookupId(this.thanas, 'thanaName', formValue.thana),
      zipCode: formValue.zipCode,
      city: formValue.city,
      mobileNumber: formValue.mobileNumber,
      email: formValue.email,
      address: formValue.address,
    };

    this.isSavingEdit = true;
    this.clientService.updateClientAddress(clientId, payload).subscribe({
      next: () => {
        this.isSavingEdit = false;
        this.alertVarient = 'success';
        this.openSuccessModal('Address updated successfully.');
        this.onEditModalClosed();
        this.loadClientAddresses();
      },
      error: () => {
        this.isSavingEdit = false;
        this.alertVarient = 'error';
        this.openSuccessModal('Failed to update address. Please try again.');
      },
    });
  }

  private parseGridEvent(event: string): AddressGridRow | null {
    try {
      return JSON.parse(event) as AddressGridRow;
    } catch (error) {
      console.error('Invalid grid row payload', error);
      return null;
    }
  }

  private resolveLocationNames(
    address: AddressGridRow['retrieveClientAddress'],
  ): void {
    const addressType$ = this.resolveAddressTypeValue(address.addressType);
    const country$ = this.resolveCountryName(address.country);
    const division$ = this.resolveDivisionName(address.division);
    const district$ = this.resolveDistrictName(address.district);
    const thana$ = this.resolveThanaName(address.thana);

    forkJoin({
      addressType: addressType$,
      country: country$,
      division: division$,
      district: district$,
      thana: thana$,
    }).subscribe((resolved) => {
      this.addressDetailsGroup.patchValue({
        addressType: resolved.addressType,
        country: resolved.country,
        division: resolved.division,
        district: resolved.district,
        thana: resolved.thana,
      });
    });
  }

  private resolveCountryName(value: string | number) {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return of(fallback);
    }
    return this.addressLookupService.getCountry(id).pipe(
      map((res) => res?.countryName ?? fallback),
      catchError(() => of(fallback)),
    );
  }

  private resolveDivisionName(value: string | number) {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return of(fallback);
    }
    return this.addressLookupService.getDivision(id).pipe(
      map((res) => res?.divisionName ?? fallback),
      catchError(() => of(fallback)),
    );
  }

  private resolveDistrictName(value: string | number) {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return of(fallback);
    }
    return this.addressLookupService.getDistrict(id).pipe(
      map((res) => res?.districtName ?? fallback),
      catchError(() => of(fallback)),
    );
  }

  private resolveThanaName(value: string | number) {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return of(fallback);
    }
    return this.addressLookupService.getThana(id).pipe(
      map((res) => res?.thanaName ?? fallback),
      catchError(() => of(fallback)),
    );
  }

  private toNumericId(value: string | number): number | null {
    if (value === null || value === undefined) {
      return null;
    }
    const numeric = Number(value);
    if (!Number.isFinite(numeric)) {
      return null;
    }
    return numeric;
  }

  private stringifyValue(value: string | number): string {
    if (value === null || value === undefined) {
      return '';
    }
    return String(value);
  }

  private loadAddressTypes(): void {
    this.addressLookupService.getTypes().subscribe({
      next: (types) => {
        this.addressTypes = types;
      },
      error: () => {
        this.alertVarient = 'error';
        this.openSuccessModal('Failed to load address types.');
      },
    });
  }

  private loadCountries(): void {
    this.addressLookupService.getCountries().subscribe({
      next: (countries) => {
        this.countries = countries;
      },
      error: () => {
        this.alertVarient = 'error';
        this.openSuccessModal('Failed to load countries.');
      },
    });
  }

  private loadDivisions(countryName: string): void {
    this.addressLookupService
      .getDivisionsByCountry(this.countries, countryName)
      .subscribe({
        next: (divisions) => {
          this.divisions = divisions;
        },
        error: () => {
          this.alertVarient = 'error';
          this.openSuccessModal('Failed to load divisions.');
        },
      });
  }

  private loadDistricts(divisionName: string): void {
    this.addressLookupService
      .getDistrictsByDivision(this.divisions, divisionName)
      .subscribe({
        next: (districts) => {
          this.districts = districts;
        },
        error: () => {
          this.alertVarient = 'error';
          this.openSuccessModal('Failed to load districts.');
        },
      });
  }

  private loadThanas(districtName: string): void {
    this.addressLookupService
      .getThanasByDistrict(this.districts, districtName)
      .subscribe({
        next: (thanas) => {
          this.thanas = thanas;
        },
        error: () => {
          this.alertVarient = 'error';
          this.openSuccessModal('Failed to load thanas.');
        },
      });
  }

  private async loadEditAddressLookups(
    address: AddressGridRow['retrieveClientAddress'],
  ): Promise<void> {
    this.divisions = [];
    this.districts = [];
    this.thanas = [];

    try {
      if (!this.addressTypes.length) {
        this.addressTypes = await firstValueFrom(
          this.addressLookupService.getTypes(),
        );
      }
      if (!this.countries.length) {
        this.countries = await firstValueFrom(
          this.addressLookupService.getCountries(),
        );
      }

      const addressTypeName = await this.resolveAddressTypeValue(
        address.addressType,
      );
      const countryName = await this.resolveCountryValue(address.country);

      this.editAddressGroup.patchValue({
        addressType: addressTypeName,
        country: countryName,
      });

      if (countryName) {
        this.divisions = await firstValueFrom(
          this.addressLookupService.getDivisionsByCountry(
            this.countries,
            countryName,
          ),
        );

        const divisionName = await this.resolveDivisionValue(address.division);
        if (divisionName) {
          this.editAddressGroup.get('division')?.setValue(divisionName);

          this.districts = await firstValueFrom(
            this.addressLookupService.getDistrictsByDivision(
              this.divisions,
              divisionName,
            ),
          );

          const districtName = await this.resolveDistrictValue(
            address.district,
          );
          if (districtName) {
            this.editAddressGroup.get('district')?.setValue(districtName);

            this.thanas = await firstValueFrom(
              this.addressLookupService.getThanasByDistrict(
                this.districts,
                districtName,
              ),
            );

            const thanaName = await this.resolveThanaValue(address.thana);
            if (thanaName) {
              this.editAddressGroup.get('thana')?.setValue(thanaName);
            }
          }
        }
      }
    } catch {
      this.editErrorMessage = 'Failed to load address data.';
    }
  }

  private async resolveAddressTypeValue(
    value: string | number,
  ): Promise<string> {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return fallback;
    }
    try {
      const type = await firstValueFrom(
        this.addressLookupService.getAddressType(id),
      );
      return type?.addressTypeName ?? fallback;
    } catch {
      return fallback;
    }
  }

  private async resolveCountryValue(value: string | number): Promise<string> {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return fallback;
    }
    try {
      const country = await firstValueFrom(
        this.addressLookupService.getCountry(id),
      );
      return country?.countryName ?? fallback;
    } catch {
      return fallback;
    }
  }

  private async resolveDivisionValue(value: string | number): Promise<string> {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return fallback;
    }
    try {
      const division = await firstValueFrom(
        this.addressLookupService.getDivision(id),
      );
      return division?.divisionName ?? fallback;
    } catch {
      return fallback;
    }
  }

  private async resolveDistrictValue(value: string | number): Promise<string> {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return fallback;
    }
    try {
      const district = await firstValueFrom(
        this.addressLookupService.getDistrict(id),
      );
      return district?.districtName ?? fallback;
    } catch {
      return fallback;
    }
  }

  private async resolveThanaValue(value: string | number): Promise<string> {
    const fallback = this.stringifyValue(value);
    const id = this.toNumericId(value);
    if (id === null) {
      return fallback;
    }
    try {
      const thana = await firstValueFrom(
        this.addressLookupService.getThana(id),
      );
      return thana?.thanaName ?? fallback;
    } catch {
      return fallback;
    }
  }

  private lookupId<T extends { id: number }>(
    list: T[],
    nameKey: keyof T,
    value: string | number,
  ): number | string {
    const numeric = this.toNumericId(value);
    if (numeric !== null) {
      return numeric;
    }
    const match = list.find((item) => item[nameKey] === value);
    return match?.id ?? this.stringifyValue(value);
  }

  private openDeleteConfirmation(clientId: number): void {
    this.pendingDeleteClientId = clientId;
    this.deleteMessage.set(
      `Are you sure you want to delete this address with client Id: ${clientId}?`,
    );
    this.isDeleteConfirmationOpen.set(true);
  }

  closeSuccessModal(): void {
    this.isSuccessModalOpen.set(false);
    this.successModalMessage.set('');
  }

  onSuccessModalButtonClick(event: { action: string }): void {
    this.closeSuccessModal();
  }

  private openSuccessModal(message: string): void {
    this.successModalMessage.set(message);
    this.isSuccessModalOpen.set(true);
  }
}
