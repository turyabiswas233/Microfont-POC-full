import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllAddress } from './all-address';

describe('AllAddress', () => {
  let component: AllAddress;
  let fixture: ComponentFixture<AllAddress>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllAddress]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllAddress);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
