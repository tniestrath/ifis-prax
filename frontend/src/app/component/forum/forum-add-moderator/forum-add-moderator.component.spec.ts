import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumAddModeratorComponent } from './forum-add-moderator.component';

describe('ForumAddModeratorComponent', () => {
  let component: ForumAddModeratorComponent;
  let fixture: ComponentFixture<ForumAddModeratorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ForumAddModeratorComponent]
    });
    fixture = TestBed.createComponent(ForumAddModeratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
