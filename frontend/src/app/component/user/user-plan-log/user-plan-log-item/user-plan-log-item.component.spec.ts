import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPlanLogItemComponent } from './user-plan-log-item.component';

describe('UserPlanLogItemComponent', () => {
  let component: UserPlanLogItemComponent;
  let fixture: ComponentFixture<UserPlanLogItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserPlanLogItemComponent]
    });
    fixture = TestBed.createComponent(UserPlanLogItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
