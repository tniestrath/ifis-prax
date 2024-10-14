import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitorSubscriptionChartComponent } from './visitor-subscription-chart.component';

describe('VisitorSubscribtionChartComponent', () => {
  let component: VisitorSubscriptionChartComponent;
  let fixture: ComponentFixture<VisitorSubscriptionChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VisitorSubscriptionChartComponent]
    });
    fixture = TestBed.createComponent(VisitorSubscriptionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
