import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumModerationListItemComponent } from './forum-moderation-list-item.component';

describe('ForumModerationListItemComponent', () => {
  let component: ForumModerationListItemComponent;
  let fixture: ComponentFixture<ForumModerationListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ForumModerationListItemComponent]
    });
    fixture = TestBed.createComponent(ForumModerationListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
