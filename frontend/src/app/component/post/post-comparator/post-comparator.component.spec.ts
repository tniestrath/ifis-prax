import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostComparatorComponent } from './post-comparator.component';

describe('PostComparatorComponent', () => {
  let component: PostComparatorComponent;
  let fixture: ComponentFixture<PostComparatorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PostComparatorComponent]
    });
    fixture = TestBed.createComponent(PostComparatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
