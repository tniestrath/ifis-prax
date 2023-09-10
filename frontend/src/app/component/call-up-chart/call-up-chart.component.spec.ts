import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CallUpChartComponent } from './call-up-chart.component';

describe('UniChartComponent', () => {
  let component: CallUpChartComponent;
  let fixture: ComponentFixture<CallUpChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CallUpChartComponent]
    });
    fixture = TestBed.createComponent(CallUpChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
