import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumModeratorComponent } from './forum-moderator.component';

describe('ForumModeratorComponent', () => {
  let component: ForumModeratorComponent;
  let fixture: ComponentFixture<ForumModeratorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ForumModeratorComponent]
    });
    fixture = TestBed.createComponent(ForumModeratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
