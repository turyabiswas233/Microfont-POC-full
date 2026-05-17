import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ClientAccountService {
  private readonly baseUrl = 'http://localhost:8199/sandbox/api/clients';

  constructor(private http: HttpClient) {}

  getClientAccounts(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl + '/account');
  }

  deleteClientAccount(clientId: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.baseUrl}/${clientId}/account`);
  }

  updateClientAccount(clientId: number, payload: any): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/${clientId}/account`, payload);
  }
}
