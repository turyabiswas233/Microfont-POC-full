import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ClientAddressService {
  private readonly baseUrl =
    'http://localhost:8199/sandbox/api/clients';

  constructor(private http: HttpClient) {}

  getClientAddresses(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl+"/address");
  }

  deleteClientAddress(clientId: number): Observable<boolean> {
    return this.http.delete<boolean>(this.baseUrl+`/${clientId}/address`);
  }

  updateClientAddress(clientId: number, payload: any): Observable<boolean> {
    return this.http.post<boolean>(this.baseUrl+`/${clientId}/address`, payload);
  }
}
