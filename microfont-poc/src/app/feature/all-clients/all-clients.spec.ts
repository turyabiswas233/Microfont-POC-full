import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllClients } from './all-clients';

describe('AllClients', () => {
  let component: AllClients;
  let fixture: ComponentFixture<AllClients>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllClients]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllClients);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
