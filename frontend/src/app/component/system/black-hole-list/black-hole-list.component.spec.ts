import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlackHoleListComponent } from './black-hole-list.component';

describe('BlackHoleListComponent', () => {
  let component: BlackHoleListComponent;
  let fixture: ComponentFixture<BlackHoleListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BlackHoleListComponent]
    });
    fixture = TestBed.createComponent(BlackHoleListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
