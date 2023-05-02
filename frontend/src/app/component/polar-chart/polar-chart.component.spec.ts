import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolarChartComponent } from './polar-chart.component';

describe('PolarChartComponent', () => {
  let component: PolarChartComponent;
  let fixture: ComponentFixture<PolarChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PolarChartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PolarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
