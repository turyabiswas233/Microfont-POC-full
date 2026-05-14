import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientAddress } from './client-address';

describe('ClientAddress', () => {
  let component: ClientAddress;
  let fixture: ComponentFixture<ClientAddress>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientAddress]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientAddress);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
