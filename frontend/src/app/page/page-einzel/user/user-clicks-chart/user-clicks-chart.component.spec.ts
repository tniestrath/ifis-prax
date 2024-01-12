import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserClicksChartComponent } from './user-clicks-chart.component';

describe('UserClicksChartComponent', () => {
  let component: UserClicksChartComponent;
  let fixture: ComponentFixture<UserClicksChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserClicksChartComponent]
    });
    fixture = TestBed.createComponent(UserClicksChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
