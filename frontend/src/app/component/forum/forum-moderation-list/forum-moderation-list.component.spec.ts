import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumModerationListComponent } from './forum-moderation-list.component';

describe('ForumModerationListComponent', () => {
  let component: ForumModerationListComponent;
  let fixture: ComponentFixture<ForumModerationListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ForumModerationListComponent]
    });
    fixture = TestBed.createComponent(ForumModerationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
