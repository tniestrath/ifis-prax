import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostListPageableComponent } from './post-list-pageable.component';

describe('PostListComponent', () => {
  let component: PostListPageableComponent;
  let fixture: ComponentFixture<PostListPageableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PostListPageableComponent]
    });
    fixture = TestBed.createComponent(PostListPageableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
