import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClicksByTimeComponent } from './clicks-by-time.component';

describe('ClicksByTimeComponent', () => {
  let component: ClicksByTimeComponent;
  let fixture: ComponentFixture<ClicksByTimeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClicksByTimeComponent]
    });
    fixture = TestBed.createComponent(ClicksByTimeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
