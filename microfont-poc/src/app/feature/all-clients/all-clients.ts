import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { GenericDataGrid } from '../../shared/common-components/generic-component-type/generic-data-grid';
import { ClientRegistrationService } from '../client-registration/service/client-registration.service';
import { GenericButton } from '../../shared/common-components/generic-component-type/generic-button/generic-button';
import { GenericModal } from '../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { ExpansionPanelHeader } from '../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { InputTextBox } from '../../shared/common-components/input-types/input-text-box/input-text-box';
import {
  ClientGridService,
  ClientGridRow,
  CLIENT_GRID_FIELDS,
} from './ClientGridRow';
import { FormControl, FormGroup } from '@angular/forms';
import { InputDate } from '../../shared/common-components/input-types/input-date/input-date';

@Component({
  selector: 'app-all-clients',
  imports: [
    GenericDataGrid,
    GenericButton,
    GenericModal,
    ExpansionPanelHeader,
    InputTextBox,
    InputDate,
  ],
  templateUrl: './all-clients.html',
  styleUrl: './all-clients.scss',
})
export class AllClients implements OnInit {
  clients: ClientGridRow[] = [];
  selectedClient: ClientGridRow | any = null;
  infoHeaderPanel: WritableSignal<boolean> = signal(false);
  personalDetailsGroup: FormGroup;
  columns = Object.keys(
    CLIENT_GRID_FIELDS,
  ) as (keyof typeof CLIENT_GRID_FIELDS)[];

  columnLabels = CLIENT_GRID_FIELDS;

  constructor(
    private clientService: ClientRegistrationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadClients();
    this.personalDetailsGroup = new FormGroup({
      clientName: new FormControl('', { nonNullable: true }),
      fatherName: new FormControl('', { nonNullable: true }),
      motherName: new FormControl('', { nonNullable: true }),
      gender: new FormControl('', { nonNullable: true }),
      maritalStatus: new FormControl('', { nonNullable: true }),
      dateOfBirth: new FormControl('', { nonNullable: true }),
      nidNumber: new FormControl('', { nonNullable: true }),
      mobileNumber: new FormControl('', { nonNullable: true }),
      email: new FormControl('', { nonNullable: true }),
      city: new FormControl('', { nonNullable: true }),
      accountTitle: new FormControl('', { nonNullable: true }),
      accountNumber: new FormControl('', { nonNullable: true }),
      limit: new FormControl(0, { nonNullable: true }),
    });
  }

  loadClients(): void {
    this.clientService.getClients().subscribe({
      next: (records) => {
        this.clients = records
          .map((item) => ClientGridService.toGridRow(item))
          .map((item) => {
            return <ClientGridRow>{
              ...item,
              dateOfBirth: new Date(item.dateOfBirth).toLocaleDateString(
                'en-UK',
                {
                  year: 'numeric',
                  month: 'short',
                  day: '2-digit',
                },
              ),
            };
          });
        console.log(records.length, this.clients.length);
      },
      error: (error) => {
        console.error('Failed to load clients list', error);
      },
    });
  }

  handleEdit(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }
    this.router.navigate(['/client-registration'], {
      queryParams: {
        clientId: row.clientId,
        mode: 'edit',
      },
    });
  }

  handleDelete(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }

    const confirmed = confirm(
      `Are you sure you want to delete client with ID ${row.clientId}: ${row.clientName}?`,
    );
    if (!confirmed) {
      return;
    }

    this.clientService.deleteClient(row.clientId).subscribe({
      next: (success) => {
        if (success) {
          this.loadClients();
        } else {
          console.error(
            'Failed to delete client: Server returned failure response',
          );
        }
      },
      error: (error) => {
        console.error('Failed to delete client', error);
      },
    });
  }

  handleModalView(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }

    localStorage.setItem('selectedClientId', row.clientId.toString());
    const selectedClientId = localStorage.getItem('selectedClientId');
    this.selectedClient = selectedClientId
      ? this.clients.find((c) => c.clientId === Number(selectedClientId))
      : null;

    this.infoHeaderPanel.set(true);
    if (this.selectedClient) {
      this.personalDetailsGroup.patchValue({
        clientName: this.selectedClient.clientName,
        fatherName: this.selectedClient.fatherName,
        motherName: this.selectedClient.motherName,
        gender: this.selectedClient.gender,
        maritalStatus: this.selectedClient.maritalStatus,
        dateOfBirth: new Date(this.selectedClient.dateOfBirth),
        nidNumber: this.selectedClient.nidNumber,
        mobileNumber: this.selectedClient.mobileNumber,
        email: this.selectedClient.email,
        city: this.selectedClient.city,
        accountTitle: this.selectedClient.accountTitle,
        accountNumber: this.selectedClient.accountNumber,
        limit: this.selectedClient.limitAmount,
      });
    }
  }

  onModalClosed(): void {
    this.selectedClient = null;
    localStorage.removeItem('selectedClientId');
  }

  handleDeleteRow(event: string): void {
    const row = this.parseGridEvent(event);
    if (!row) {
      return;
    }

    this.clientService.deleteClient(row.clientId).subscribe({
      next: () => {
        this.clients = this.clients.filter(
          (item) => item.clientId !== row.clientId,
        );
      },
      error: (error) => {
        console.error('Failed to delete client', error);
      },
    });
  }

  private parseGridEvent(event: string): ClientGridRow | null {
    try {
      return JSON.parse(event) as ClientGridRow;
    } catch (error) {
      console.error('Invalid grid row payload', error);
      return null;
    }
  }

  public handleCreateNew(): void {
    this.router.navigate(['/client-registration']);
  }
}
