import {
  Component,
  effect,
  inject,
  signal,
  SimpleChanges,
} from '@angular/core';
import { Subscription, firstValueFrom, forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ClientInfo } from '../client-info/client-info';
import { ClientDetails } from '../client-details/client-details';
import { ClientAddress } from '../client-address/client-address';
import { ClientAccountinfo } from '../client-accountinfo/client-accountinfo';
import { CustomAlert } from '../custom-alert/custom-alert';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  ClientRegistrationService,
  ClientRecord,
} from './service/client-registration.service';
import {
  ButtonUtils,
  FormGroupSignal,
  ONCLICK_RESET,
  ONCLICK_SAVE,
  ONCLICK_UPDATE,
} from '../../shared/constant/button-signals.constant';
import { AddressService } from './service/address.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-client-registration',
  imports: [
    ClientInfo,
    ClientDetails,
    ClientAddress,
    ClientAccountinfo,
    CustomAlert,
    ReactiveFormsModule,
  ],
  templateUrl: './client-registration.html',
  styleUrl: './client-registration.scss',
})
export class ClientRegistration {
  private router = inject(Router);
  clientFb = inject(FormBuilder);
  clientService = inject(ClientRegistrationService);
  addressService = inject(AddressService);
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

  clientForm: FormGroup = this.clientFb.group({
    clientInfo: this.clientFb.group({
      clientName: ['', Validators.required],
      clientId: [-1, Validators.required],
    }),
    clientDetails: this.clientFb.group({
      fatherName: ['', Validators.required],
      motherName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      spouseName: [''],
      gender: ['', Validators.required],
      maritalStatus: ['', Validators.required],
      nidNumber: ['', Validators.required],
    }),
    clientAddress: this.clientFb.group({
      addressType: ['', Validators.required],
      country: ['', Validators.required],
      division: ['', Validators.required],
      district: ['', Validators.required],
      thana: ['', Validators.required],
      zipCode: [''],
      city: ['', Validators.required],
      mobileNumber: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required],
    }),
    clientAccountInfo: this.clientFb.group({
      officeCode: ['', Validators.required],
      accountNumber: ['', Validators.required],
      accountTitle: ['', Validators.required],
      accountOpenDate: ['', Validators.required],
      accountExpiryDate: [''],
      limitAmount: [0, Validators.required],
    }),
  });
  clients: ClientRecord[] = [];
  clientId: number | null = null;

  private countrySub?: Subscription;
  isSaving = false;
  errorMessage: string | null = null;
  mode: 'edit' | 'create' = 'create';
  isSuccessModalOpen = signal(false);
  successModalTitle = signal('Success!');
  successModalMessage = signal('');
  successAction: 'create' | 'update' | null = null;

  get clientInfoForm(): FormGroup {
    return this.clientForm.get('clientInfo') as FormGroup;
  }
  get clientDetailsForm(): FormGroup {
    return this.clientForm.get('clientDetails') as FormGroup;
  }
  get addressContactForm(): FormGroup {
    return this.clientForm.get('addressContact') as FormGroup;
  }
  get clientAddressForm(): FormGroup {
    return this.clientForm.get('clientAddress') as FormGroup;
  }

  get accountInfoForm(): FormGroup {
    return this.clientForm.get('clientAccountInfo') as FormGroup;
  }

  constructor() {
    effect(() => {
      if (ONCLICK_SAVE()) {
        this.onSubmit();
        ONCLICK_SAVE.set(false);
      }
    });

    effect(() => {
      if (ONCLICK_RESET()) {
        this.onReset();
        ONCLICK_RESET.set(false);
      }
    });
    effect(() => {
      if (ONCLICK_UPDATE()) {
        this.onUpdate();
        ONCLICK_UPDATE.set(false);
      }
    });
  }

  ngOnInit(): void {
    // check if edit mode or not

    const query = new URLSearchParams(window.location.search);
    this.mode = (query.get('mode') as 'edit' | 'create') || 'create';
    const clientIdParam = query.get('clientId');
    this.clientId = clientIdParam ? parseInt(clientIdParam) : -1;

    if (this.clientId && this.mode === 'edit') {
      void this.loadClient(this.clientId);
    } else {
      // do nothing
      this.loadAddressTypes();
      this.loadCountries();
    }
    this.syncDependentFormsState();
  }

  ngOnDestroy(): void {
    this.countrySub?.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clientId'] && this.clientId) {
      this.syncDependentFormsState();
    }
  }

  onCountrySelected(countryName: string): void {
    this.divisions = [];
    this.districts = [];
    this.thanas = [];
    this.clientAddressForm.patchValue({
      division: '',
      district: '',
      thana: '',
    });

    if (typeof countryName === 'string' && countryName.trim()) {
      this.loadDivisions(countryName);
    }
  }

  onDivisionSelected(divisionName: string): void {
    this.districts = [];
    this.thanas = [];
    this.clientAddressForm.patchValue({
      district: '',
      thana: '',
    });

    if (typeof divisionName === 'string' && divisionName.trim()) {
      this.loadDistricts(divisionName);
    }
  }

  onDistrictSelected(districtName: string): void {
    this.thanas = [];
    this.clientAddressForm.patchValue({
      thana: '',
    });

    if (typeof districtName === 'string' && districtName.trim()) {
      this.loadThanas(districtName);
    }
  }

  private syncDependentFormsState(): void {
    const hasClientId = this.clientId != -1 || this.mode === 'edit';

    FormGroupSignal.set(this.clientForm);
    ButtonUtils.setPageButtons({
      save: this.mode.toString() !== 'edit',
      update: this.mode.toString() === 'edit',
      reset: true,
    });

    if (!hasClientId) {
      this.clientDetailsForm.disable();
      this.clientAddressForm.disable();
      this.accountInfoForm.disable();
    } else {
      this.clientDetailsForm.enable();
      this.clientAddressForm.enable();
      this.accountInfoForm.enable();
    }
  }

  private loadDivisions(countryName: string): void {
    this.addressService
      .getDivisionsByCountry(this.countries, countryName)
      .subscribe({
        next: (divisions) => {
          this.divisions = divisions;
        },
        error: () => {
          this.errorMessage = 'Failed to load divisions.';
        },
      });
  }

  private loadDistricts(divisionName: string): void {
    this.addressService
      .getDistrictsByDivision(this.divisions, divisionName)
      .subscribe({
        next: (districts) => {
          this.districts = districts;
        },
        error: () => {
          this.errorMessage = 'Failed to load districts.';
        },
      });
  }

  private loadThanas(districtName: string): void {
    this.addressService
      .getThanasByDistrict(this.districts, districtName)
      .subscribe({
        next: (thanas) => {
          this.thanas = thanas;
        },
        error: () => {
          this.errorMessage = 'Failed to load thanas.';
        },
      });
  }

  private loadCountries(): void {
    this.addressService.getCountries().subscribe({
      next: (countries) => {
        this.countries = countries;
      },
      error: () => {
        this.errorMessage = 'Failed to load countries.';
      },
    });
  }

  private loadAddressTypes(): void {
    this.addressService.getTypes().subscribe({
      next: (types) => {
        this.addressTypes = types;
      },
      error: () => {
        this.errorMessage = 'Failed to load address types.';
      },
    });
  }

  onReset(): void {
    this.clientForm.reset();
  }

  async loadClient(id: number): Promise<void> {
    try {
      const client = await firstValueFrom(this.clientService.getClientById(id));
      this.clientId = client.id;
      const address = client.retrieveClientAddress;
      const resolvedLocationNames = await firstValueFrom(
        this.resolveLocationNames(address),
      );

      this.clientInfoForm.patchValue({
        clientName: client.retrieveClientInfo.clientName,
        clientId: client.retrieveClientInfo.clientId,
      });

      this.clientDetailsForm.patchValue({
        fatherName: client.retrieveClientDetails.fatherName,
        motherName: client.retrieveClientDetails.motherName,
        dateOfBirth: client.retrieveClientDetails.dateOfBirth,
        spouseName: client.retrieveClientDetails.spouseName,
        gender: client.retrieveClientDetails.gender,
        maritalStatus: client.retrieveClientDetails.maritalStatus,
        nidNumber: client.retrieveClientDetails.nidNumber,
      });

      this.clientAddressForm.patchValue({
        addressType: resolvedLocationNames.addressType,
        country: resolvedLocationNames.country,
        division: resolvedLocationNames.division,
        district: resolvedLocationNames.district,
        thana: resolvedLocationNames.thana,
        zipCode: address.zipCode,
        city: address.city,
        mobileNumber: address.mobileNumber,
        email: address.email,
        address: address.address,
      });

      this.accountInfoForm.patchValue({
        officeCode: client.retrieveClientAccountInfo.officeCode,
        accountNumber: client.retrieveClientAccountInfo.accountNumber,
        accountTitle: client.retrieveClientAccountInfo.accountTitle,
        accountOpenDate: client.retrieveClientAccountInfo.accountOpenDate,
        accountExpiryDate: client.retrieveClientAccountInfo.accountExpiryDate,
        limitAmount: client.retrieveClientAccountInfo.limitAmount,
      });

      await this.loadAddressOptionLists(resolvedLocationNames);

      this.syncDependentFormsState();
      this.errorMessage = null;
    } catch {
      this.errorMessage = 'Failed to load client.';
    }
  }

  private resolveLocationNames(address: {
    addressType: string | number;
    country: string | number;
    division: string | number;
    district: string | number;
    thana: string | number;
  }) {
    const addressType$ = this.resolveAddressTypeValue(address.addressType);
    const country$ = this.resolveCountryValue(address.country);
    const division$ = this.resolveDivisionValue(address.division);
    const district$ = this.resolveDistrictValue(address.district);
    const thana$ = this.resolveThanaValue(address.thana);

    return forkJoin({
      addressType: addressType$,
      country: country$,
      division: division$,
      district: district$,
      thana: thana$,
    });
  }

  private async loadAddressOptionLists(address: {
    country: string;
    division: string;
    district: string;
  }): Promise<void> {
    this.divisions = [];
    this.districts = [];
    this.thanas = [];

    try {
      if (!this.addressTypes.length) {
        this.addressTypes = await firstValueFrom(
          this.addressService.getTypes(),
        );
      }

      if (!this.countries.length) {
        this.countries = await firstValueFrom(
          this.addressService.getCountries(),
        );
      }

      if (address.country) {
        this.divisions = await firstValueFrom(
          this.addressService.getDivisionsByCountry(
            this.countries,
            address.country,
          ),
        );
      }

      if (address.division) {
        this.districts = await firstValueFrom(
          this.addressService.getDistrictsByDivision(
            this.divisions,
            address.division,
          ),
        );
      }

      if (address.district) {
        this.thanas = await firstValueFrom(
          this.addressService.getThanasByDistrict(
            this.districts,
            address.district,
          ),
        );
      }
    } catch {
      this.errorMessage = 'Failed to load address data.';
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
      const type = await firstValueFrom(this.addressService.getAddressType(id));
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
      const country = await firstValueFrom(this.addressService.getCountry(id));
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
        this.addressService.getDivision(id),
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
        this.addressService.getDistrict(id),
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
      const thana = await firstValueFrom(this.addressService.getThana(id));
      return thana?.thanaName ?? fallback;
    } catch {
      return fallback;
    }
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

  onSubmit(): void {
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      return;
    }

    const payload: {
      registerClientDetails: any;
      registerClientAddress: any;
      registerClientAccountInfo: any;
    } = {
      registerClientDetails: this.clientDetailsForm.value,
      registerClientAddress: {
        ...this.clientAddressForm.value,
        addressType: this.addressTypes.find(
          (at) =>
            at.addressTypeName ===
            this.clientAddressForm.get('addressType')?.value,
        )?.id,
        country: this.countries.find(
          (c) => c.countryName === this.clientAddressForm.get('country')?.value,
        )?.id,
        division: this.divisions.find(
          (d) =>
            d.divisionName === this.clientAddressForm.get('division')?.value,
        )?.id,
        district: this.districts.find(
          (d) =>
            d.districtName === this.clientAddressForm.get('district')?.value,
        )?.id,
        thana: this.thanas.find(
          (t) => t.thanaName === this.clientAddressForm.get('thana')?.value,
        )?.id,
      },
      registerClientAccountInfo: this.accountInfoForm.value,
    };

    if (this.clientId) {
      this.createClient(payload);
    }
  }

  onUpdate(): void {
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      return;
    }

    if (!this.clientId) {
      try {
        this.clientId = this.clientInfoForm.get('clientId')?.value;
        if (!this.clientId) {
          alert('Client ID is missing!'); // Debugging alert
          throw new Error('Client ID is missing in the form.');
        }
      } catch (error) {
        this.errorMessage = 'Client ID is required for update.';
        alert('Client ID is missing!'); // Debugging alert
        return;
      }
    }

    const payload: {
      registerClientDetails: any;
      registerClientAddress: any;
      registerClientAccountInfo: any;
    } = {
      registerClientDetails: this.clientDetailsForm.value,
      registerClientAddress: {
        ...this.clientAddressForm.value,
        addressType: this.addressTypes.find(
          (at) =>
            at.addressTypeName ===
            this.clientAddressForm.get('addressType')?.value,
        )?.id,
        country: this.countries.find(
          (c) => c.countryName === this.clientAddressForm.get('country')?.value,
        )?.id,
        division: this.divisions.find(
          (d) =>
            d.divisionName === this.clientAddressForm.get('division')?.value,
        )?.id,
        district: this.districts.find(
          (d) =>
            d.districtName === this.clientAddressForm.get('district')?.value,
        )?.id,
        thana: this.thanas.find(
          (t) => t.thanaName === this.clientAddressForm.get('thana')?.value,
        )?.id,
      },
      registerClientAccountInfo: this.accountInfoForm.value,
    };

    this.updateClient(payload);
  }

  resetForm(): void {
    this.clientForm.reset();
    this.clientId = null;
    this.syncDependentFormsState();

    this.errorMessage = null;
  }

  exitForm(): void {
    this.resetForm();
    history.back();
  }

  createClient(payload: any): void {
    this.isSaving = true;
    this.clientService.createClient(this.clientId!, payload).subscribe({
      next: (client) => {
        this.isSaving = false;
        this.clientId = client.id;
        this.errorMessage = null;
        this.openSuccessModal(
          'Client registration completed successfully.',
          'create',
        );
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Failed to create client.';
      },
    });
  }

  updateClient(payload: any): void {
    this.isSaving = true;
    this.clientService.updateClient(this.clientId!, payload).subscribe({
      next: (client) => {
        this.isSaving = false;
        this.clientId = client.id;
        this.errorMessage = null;
        this.openSuccessModal('Client updated successfully.', 'update');
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Failed to update client.';
      },
    });
  }

  closeSuccessModal(): void {
    this.isSuccessModalOpen.set(false);
    this.successModalMessage.set('');
  }

  onSuccessModalButtonClick(): void {
    const action = this.successAction;
    this.successAction = null;
    this.closeSuccessModal();

    if (action) {
      this.router.navigate(['/allclients']);
    }
  }

  private openSuccessModal(message: string, action: 'create' | 'update'): void {
    this.successAction = action;
    this.successModalMessage.set(message);
    this.isSuccessModalOpen.set(true);
  }

  onGenIdClick(): void {
    const clientName = this.clientInfoForm.get('clientName')?.value;

    if (!clientName) {
      this.errorMessage = 'Client name is required before generating ID.';
      return;
    }

    this.isSaving = true;
    this.clientService.createClientWithName(clientName).subscribe({
      next: (client: { clientId: number; clientName: string }) => {
        this.clientId = client.clientId;
        this.clientInfoForm.patchValue({
          clientId: client.clientId,
        });
        this.loadAddressTypes();
        this.loadCountries();

        this.syncDependentFormsState();
        this.isSaving = false;
        this.errorMessage = null;
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Failed to generate client ID.';
      },
    });
  }
}
