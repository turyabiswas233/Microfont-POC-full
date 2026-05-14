import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

type ControlValue<T> = T extends import('@angular/forms').FormControl<infer V>
  ? V
  : never;

type FormValue<T> = {
  [K in keyof T]: ControlValue<T[K]>;
};

@Injectable({ providedIn: 'root' })
export class AddressService {
  private readonly baseUrl = 'http://localhost:8199/sandbox/api/addresses';

  constructor(private http: HttpClient) {}
  getTypes(): Observable<
    {
      addressTypeName: string;
      id: number;
    }[]
  > {
    return this.http.get<
      {
        addressTypeName: string;
        id: number;
      }[]
    >(`${this.baseUrl}/types`);
  }
  getCountries(): Observable<
    {
      countryName: string;
      id: number;
    }[]
  > {
    return this.http.get<
      {
        countryName: string;
        id: number;
      }[]
    >(`${this.baseUrl}/countries`);
  }

  getDivisionsByCountry(
    countries: {
      countryName: string;
      id: number;
    }[],
    countryName: string,
  ): Observable<
    {
      divisionName: string;
      id: number;
    }[]
  > {
    const selectedCountry = countries.find(
      (c) => c.countryName === countryName,
    );
    return this.http.get<
      {
        divisionName: string;
        id: number;
      }[]
    >(`${this.baseUrl}/divisions/${selectedCountry?.id}`);
  }

  getDistrictsByDivision(
    divisions: {
      divisionName: string;
      id: number;
    }[],
    divisionName: string,
  ): Observable<
    {
      districtName: string;
      id: number;
    }[]
  > {
    const selectedDivision = divisions.find(
      (d) => d.divisionName === divisionName,
    );
    return this.http.get<
      {
        districtName: string;
        id: number;
      }[]
    >(`${this.baseUrl}/districts/${selectedDivision?.id}`);
  }

  getThanasByDistrict(
    districts: {
      districtName: string;
      id: number;
    }[],
    districtName: string,
  ): Observable<
    {
      thanaName: string;
      id: number;
    }[]
  > {
    const selectedDistrict = districts.find(
      (d) => d.districtName === districtName,
    );
    return this.http.get<
      {
        thanaName: string;
        id: number;
      }[]
    >(`${this.baseUrl}/thanas/${selectedDistrict?.id}`);
  }

  getAddressType(id: number): Observable<{
    addressTypeName: string;
    id: number;
  }> {
    return this.http.get<{
      addressTypeName: string;
      id: number;
    }>(`${this.baseUrl}/type/${id}`);
  }

  getCountry(id: number): Observable<{
    countryName: string;
    id: number;
  }> {
    return this.http.get<{
      countryName: string;
      id: number;
    }>(`${this.baseUrl}/country/${id}`);
  }
  getDivision(id: number): Observable<{
    divisionName: string;
    id: number;
  }> {
    return this.http.get<{
      divisionName: string;
      id: number;
    }>(`${this.baseUrl}/division/${id}`);
  }
  getDistrict(id: number): Observable<{
    districtName: string;
    id: number;
  }> {
    return this.http.get<{
      districtName: string;
      id: number;
    }>(`${this.baseUrl}/district/${id}`);
  }
  getThana(id: number): Observable<{
    thanaName: string;
    id: number;
  }> {
    return this.http.get<{
      thanaName: string;
      id: number;
    }>(`${this.baseUrl}/thana/${id}`);
  }
}
