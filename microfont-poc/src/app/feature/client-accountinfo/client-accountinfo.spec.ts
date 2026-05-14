import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientAccountinfo } from './client-accountinfo';

describe('ClientAccountinfo', () => {
  let component: ClientAccountinfo;
  let fixture: ComponentFixture<ClientAccountinfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientAccountinfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientAccountinfo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
