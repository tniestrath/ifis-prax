import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSubsComponent } from './user-subs.component';

describe('UserSubsComponent', () => {
  let component: UserSubsComponent;
  let fixture: ComponentFixture<UserSubsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserSubsComponent]
    });
    fixture = TestBed.createComponent(UserSubsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
