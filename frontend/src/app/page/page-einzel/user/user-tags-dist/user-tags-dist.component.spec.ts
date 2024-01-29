import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserTagsDistComponent } from './user-tags-dist.component';

describe('UserTagsDistComponent', () => {
  let component: UserTagsDistComponent;
  let fixture: ComponentFixture<UserTagsDistComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserTagsDistComponent]
    });
    fixture = TestBed.createComponent(UserTagsDistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
