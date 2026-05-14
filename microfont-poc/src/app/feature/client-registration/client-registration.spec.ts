import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ClientRegistration } from './client-registration';

describe('ClientRegistration', () => {
  let component: ClientRegistration;
  let fixture: ComponentFixture<ClientRegistration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientRegistration],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientRegistration);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable dependent forms when clientId is cleared', () => {
    component.clientInfoForm.get('clientId')?.setValue(123);
    expect(component.clientAddressForm.disabled).toBeFalse();

    spyOn((component as any).clientService, 'deleteClient').and.returnValue(
      of(void 0),
    );

    component.deleteClient(123);

    expect(component.clientId).toBeNull();
    expect(component.clientDetailsForm.disabled).toBeTrue();
    expect(component.clientAddressForm.disabled).toBeTrue();
    expect(component.accountInfoForm.disabled).toBeTrue();
  });
});
