import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumModerationDisplayComponent } from './forum-moderation-display.component';

describe('ForumModerationDisplayComponent', () => {
  let component: ForumModerationDisplayComponent;
  let fixture: ComponentFixture<ForumModerationDisplayComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ForumModerationDisplayComponent]
    });
    fixture = TestBed.createComponent(ForumModerationDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
