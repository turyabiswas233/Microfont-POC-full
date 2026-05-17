import {
  Component,
  inject,
  numberAttribute,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { Router } from '@angular/router';
import { ClientAccountService } from '../client-registration/service/client-account.service';
import { GenericDataGrid } from '../../shared/common-components/generic-component-type/generic-data-grid';
import { GenericModal } from '../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import { InputDate } from '../../shared/common-components/input-types/input-date/input-date';
import { ClientAccountinfo } from '../client-accountinfo/client-accountinfo';
import { GenericButton } from '../../shared/common-components/generic-component-type/generic-button/generic-button';
import { FormControl, FormGroup, Validators } from '@angular/forms';

type AccountGridRow = {
  retrieveClientInfo: {
    clientId: number;
    clientName: string;
  };
  retrieveClientAccountInfo: {
    officeCode: string;
    accountNumber: string;
    accountTitle: string;
    accountOpenDate: string;
    accountExpiryDate: string;
    limitAmount: number;
  };
};

const ACCOUNT_GRID_FIELDS = {
  'retrieveClientInfo.clientId': 'Client ID',
  'retrieveClientInfo.clientName': 'Client Name',
  'retrieveClientAccountInfo.officeCode': 'Office Code',
  'retrieveClientAccountInfo.accountNumber': 'Account Number',
  'retrieveClientAccountInfo.accountTitle': 'Account Title',
  'retrieveClientAccountInfo.accountOpenDate': 'Account Open Date',
  'retrieveClientAccountInfo.accountExpiryDate': 'Account Expiry Date',
  'retrieveClientAccountInfo.limitAmount': 'Limit Amount',
} as const;

@Component({
  selector: 'app-all-accounts',
  imports: [
    ClientAccountinfo,
    GenericDataGrid,
    GenericButton,
    GenericModal,
    ExpansionPanelHeader,
    InputTextBox,
    InputDate,
  ],
  templateUrl: './all-accounts.html',
  styleUrl: './all-accounts.scss',
})
export class AllAccounts implements OnInit {
  clientAccounts: AccountGridRow[] = [];
  selectedAccount: AccountGridRow | null = null;
  editAccountRow: AccountGridRow | null = null;
  infoHeaderPanel: WritableSignal<boolean> = signal(false);
  editInfoHeaderPanel: WritableSignal<boolean> = signal(false);
  accountDetailsGroup: FormGroup;
  editAccountGroup: FormGroup;
  editErrorMessage: string | null = null;
  isSavingEdit = false;
  columns = Object.keys(
    ACCOUNT_GRID_FIELDS,
  ) as (keyof typeof ACCOUNT_GRID_FIELDS)[];
  columnLabels = ACCOUNT_GRID_FIELDS;

  allAccountService = inject(ClientAccountService);
  router = inject(Router);

  ngOnInit(): void {
    this.loadClientAccounts();
    this.accountDetailsGroup = new FormGroup({
      clientId: new FormControl('', { nonNullable: true }),
      clientName: new FormControl('', { nonNullable: true }),
      officeCode: new FormControl('', { nonNullable: true }),
      accountNumber: new FormControl('', { nonNullable: true }),
      accountTitle: new FormControl('', { nonNullable: true }),
      accountOpenDate: new FormControl('', { nonNullable: true }),
      accountExpiryDate: new FormControl('', { nonNullable: true }),
      limitAmount: new FormControl(0, { nonNullable: true }),
    });
    this.editAccountGroup = new FormGroup({
      clientId: new FormControl('', { nonNullable: true }),
      clientName: new FormControl('', { nonNullable: true }),
      officeCode: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      accountNumber: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      accountTitle: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      accountOpenDate: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      accountExpiryDate: new FormControl('', { nonNullable: true }),
      limitAmount: new FormControl(0, {
        nonNullable: true,
        validators: [Validators.required],
      }),
    });
    console.log('AllAccounts component initialized', this.clientAccounts);
  }

  loadClientAccounts(): void {
    this.allAccountService.getClientAccounts().subscribe((accounts) => {
      this.clientAccounts = accounts;
    });
  }

  handleDelete(event: string): void {
    const clientId = this.parseGridEvent(event)?.retrieveClientInfo.clientId;
    console.log('Delete account for client ID:', clientId);

    const confirmation = confirm(
      'Are you sure you want to delete this account with client Id: ' +
        clientId +
        '?',
    );
    if (confirmation && clientId !== undefined && clientId !== null) {
      this.allAccountService
        .deleteClientAccount(numberAttribute(clientId))
        .subscribe((isDeleted) => {
          if (isDeleted) {
            alert('Account deleted successfully.');
            this.loadClientAccounts();
          }
        });
    }
  }

  handleModalView(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }

    this.editAccountRow = null;
    this.selectedAccount = row;
    this.infoHeaderPanel.set(true);

    this.accountDetailsGroup.patchValue({
      clientId: row.retrieveClientInfo.clientId,
      clientName: row.retrieveClientInfo.clientName,
      officeCode: row.retrieveClientAccountInfo.officeCode,
      accountNumber: row.retrieveClientAccountInfo.accountNumber,
      accountTitle: row.retrieveClientAccountInfo.accountTitle,
      accountOpenDate: row.retrieveClientAccountInfo.accountOpenDate
        ? new Date(row.retrieveClientAccountInfo.accountOpenDate)
        : '',
      accountExpiryDate: row.retrieveClientAccountInfo.accountExpiryDate
        ? new Date(row.retrieveClientAccountInfo.accountExpiryDate)
        : '',
      limitAmount: row.retrieveClientAccountInfo.limitAmount,
    });
  }

  handleModalEdit(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }

    this.selectedAccount = null;
    this.editAccountRow = row;
    this.editErrorMessage = null;

    this.editAccountGroup.patchValue({
      clientId: row.retrieveClientInfo.clientId,
      clientName: row.retrieveClientInfo.clientName,
      officeCode: row.retrieveClientAccountInfo.officeCode,
      accountNumber: row.retrieveClientAccountInfo.accountNumber,
      accountTitle: row.retrieveClientAccountInfo.accountTitle,
      accountOpenDate: row.retrieveClientAccountInfo.accountOpenDate
        ? new Date(row.retrieveClientAccountInfo.accountOpenDate)
        : '',
      accountExpiryDate: row.retrieveClientAccountInfo.accountExpiryDate
        ? new Date(row.retrieveClientAccountInfo.accountExpiryDate)
        : '',
      limitAmount: row.retrieveClientAccountInfo.limitAmount,
    });
  }

  onModalClosed(): void {
    this.selectedAccount = null;
  }

  onEditModalClosed(): void {
    this.editAccountRow = null;
    this.editErrorMessage = null;
    this.editAccountGroup.reset();
  }

  saveEdit(): void {
    if (this.editAccountGroup.invalid) {
      this.editAccountGroup.markAllAsTouched();
      return;
    }

    const clientId = Number(this.editAccountGroup.get('clientId')?.value);
    if (!Number.isFinite(clientId)) {
      alert('Client ID is missing.');
      return;
    }

    const formValue = this.editAccountGroup.getRawValue();
    const payload = {
      officeCode: formValue.officeCode,
      accountNumber: formValue.accountNumber,
      accountTitle: formValue.accountTitle,
      accountOpenDate: formValue.accountOpenDate,
      accountExpiryDate: formValue.accountExpiryDate,
      limitAmount: formValue.limitAmount,
    };

    this.isSavingEdit = true;
    this.allAccountService.updateClientAccount(clientId, payload).subscribe({
      next: () => {
        this.isSavingEdit = false;
        alert('Account updated successfully.');
        this.onEditModalClosed();
        this.loadClientAccounts();
      },
      error: () => {
        this.isSavingEdit = false;
        this.editErrorMessage = 'Failed to update account.';
      },
    });
  }

  private parseGridEvent(event: string): AccountGridRow | null {
    try {
      return JSON.parse(event) as AccountGridRow;
    } catch (error) {
      console.error('Invalid grid row payload', error);
      return null;
    }
  }
}
