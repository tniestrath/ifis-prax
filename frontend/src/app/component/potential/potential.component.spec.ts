import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PotentialComponent } from './potential.component';

describe('PotentialComponent', () => {
  let component: PotentialComponent;
  let fixture: ComponentFixture<PotentialComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PotentialComponent]
    });
    fixture = TestBed.createComponent(PotentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
