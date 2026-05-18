import {
  Component,
  effect,
  inject,
  signal,
  SimpleChanges,
} from '@angular/core';
import { Subscription, firstValueFrom } from 'rxjs';
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
      this.loadClient(this.clientId);
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

  loadClient(id: number): void {
    this.clientService.getClientById(id).subscribe({
      next: (client) => {
        this.clientId = client.id;
        const address = client.retrieveClientAddress;
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
          addressType: address.addressType,
          country: address.country,
          division: address.division,
          district: address.district,
          thana: address.thana,
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
        this.loadAddressLookups(address);

        this.syncDependentFormsState();

        this.errorMessage = null;
      },
      error: () => {
        this.errorMessage = 'Failed to load client.';
      },
      complete: () => {
        this.syncDependentFormsState();
      },
    });
  }

  private async loadAddressLookups(address: {
    addressType: number;
    country: number;
    division: number;
    district: number;
    thana: number;
  }): Promise<void> {
    this.divisions = [];
    this.districts = [];
    this.thanas = [];

    const errorMessages: Record<string, string> = {
      addressTypes: 'Failed to load address types.',
      addressType: 'Failed to load address type.',
      countries: 'Failed to load countries.',
      country: 'Failed to load country.',
      divisions: 'Failed to load divisions.',
      division: 'Failed to load division.',
      districts: 'Failed to load districts.',
      district: 'Failed to load district.',
      thanas: 'Failed to load thanas.',
      thana: 'Failed to load thana.',
    };

    let step = 'addressTypes';

    try {
      this.addressTypes = await firstValueFrom(this.addressService.getTypes());

      step = 'addressType';
      if (this.addressTypes.some((at) => at.id === address.addressType)) {
        const addressType = await firstValueFrom(
          this.addressService.getAddressType(address.addressType),
        );
        this.clientAddressForm
          .get('addressType')
          ?.setValue(addressType.addressTypeName);
      }

      step = 'countries';

      this.countries = await firstValueFrom(this.addressService.getCountries());
      if (this.countries.some((c) => c.id === address.country)) {
        step = 'country';
        const country = await firstValueFrom(
          this.addressService.getCountry(address.country),
        );
        this.clientAddressForm.get('country')?.setValue(country.countryName);

        step = 'divisions';
        this.divisions = await firstValueFrom(
          this.addressService.getDivisionsByCountry(
            this.countries,
            country.countryName,
          ),
        );
        if (this.divisions.some((d) => d.id === address.division)) {
          step = 'division';
          const division = await firstValueFrom(
            this.addressService.getDivision(address.division),
          );
          this.clientAddressForm
            .get('division')
            ?.setValue(division.divisionName);

          step = 'districts';
          this.districts = await firstValueFrom(
            this.addressService.getDistrictsByDivision(
              this.divisions,
              division.divisionName,
            ),
          );

          if (this.districts.some((d) => d.id === address.district)) {
            step = 'district';
            const district = await firstValueFrom(
              this.addressService.getDistrict(address.district),
            );
            this.clientAddressForm
              .get('district')
              ?.setValue(district.districtName);

            step = 'thanas';
            this.thanas = await firstValueFrom(
              this.addressService.getThanasByDistrict(
                this.districts,
                district.districtName,
              ),
            );
          }
        }
      }
      step = 'thana';
      const thana = await firstValueFrom(
        this.addressService.getThana(address.thana),
      );
      this.clientAddressForm.get('thana')?.setValue(thana.thanaName);
    } catch {
      this.errorMessage = errorMessages[step] ?? 'Failed to load address data.';
    }
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
