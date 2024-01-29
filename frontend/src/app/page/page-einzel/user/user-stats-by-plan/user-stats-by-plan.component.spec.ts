import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserStatsByPlanComponent } from './user-stats-by-plan.component';

describe('UserStatsByPlanComponent', () => {
  let component: UserStatsByPlanComponent;
  let fixture: ComponentFixture<UserStatsByPlanComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserStatsByPlanComponent]
    });
    fixture = TestBed.createComponent(UserStatsByPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
