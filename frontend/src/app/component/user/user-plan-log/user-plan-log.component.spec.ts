import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPlanLogComponent } from './user-plan-log.component';

describe('UserPlanLogComponent', () => {
  let component: UserPlanLogComponent;
  let fixture: ComponentFixture<UserPlanLogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserPlanLogComponent]
    });
    fixture = TestBed.createComponent(UserPlanLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
