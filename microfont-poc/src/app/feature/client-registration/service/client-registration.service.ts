import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

type ControlValue<T> = T extends import('@angular/forms').FormControl<infer V>
  ? V
  : never;

type FormValue<T> = {
  [K in keyof T]: ControlValue<T[K]>;
};

export type ClientRecord = any;

@Injectable({ providedIn: 'root' })
export class ClientRegistrationService {
  private readonly baseUrl = 'http://localhost:8199/sandbox/api/clients';

  constructor(private http: HttpClient) {}

  getClients(): Observable<ClientRecord[]> {
    return this.http.get<ClientRecord[]>(this.baseUrl);
  }

  getClientById(id: number): Observable<ClientRecord> {
    return this.http.get<ClientRecord>(`${this.baseUrl}/${id}`);
  }

  createClient(id: number, payload: any): Observable<ClientRecord> {
    console.log('Creating client with payload:', payload);
    return this.http.post<ClientRecord>(
      `${this.baseUrl}/${id}/create`,
      payload,
    );
  }

  createClientWithName(
    clientName: string,
  ): Observable<{ clientId: number; clientName: string; updatedAt: Date }> {
    return this.http.post<{
      clientId: number;
      clientName: string;
      updatedAt: Date;
    }>(`${this.baseUrl}/genid`, {
      clientName,
    });
  }

  updateClient(id: number, payload: any): Observable<ClientRecord> {
    console.log('Updating client with payload:', payload);
    return this.http.put<ClientRecord>(`${this.baseUrl}/${id}`, payload);
  }

  deleteClient(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.baseUrl}/${id}`);
  }
}
