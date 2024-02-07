import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserTagDistItemComponent } from './user-tag-dist-item.component';

describe('UserTagDistItemComponent', () => {
  let component: UserTagDistItemComponent;
  let fixture: ComponentFixture<UserTagDistItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserTagDistItemComponent]
    });
    fixture = TestBed.createComponent(UserTagDistItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
