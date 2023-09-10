import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UniChartComponent } from './origin-by-time-chart.component';

describe('UniChartComponent', () => {
  let component: UniChartComponent;
  let fixture: ComponentFixture<UniChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UniChartComponent]
    });
    fixture = TestBed.createComponent(UniChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
