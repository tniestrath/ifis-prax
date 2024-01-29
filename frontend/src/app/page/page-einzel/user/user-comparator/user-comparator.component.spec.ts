import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserComparatorComponent } from './user-comparator.component';

describe('UserComparatorComponent', () => {
  let component: UserComparatorComponent;
  let fixture: ComponentFixture<UserComparatorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserComparatorComponent]
    });
    fixture = TestBed.createComponent(UserComparatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
