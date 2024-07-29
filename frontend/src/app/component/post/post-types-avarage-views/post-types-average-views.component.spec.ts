import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostTypesAverageViewsComponent } from './post-types-average-views.component';

describe('PostTypesAvarageViewsComponent', () => {
  let component: PostTypesAverageViewsComponent;
  let fixture: ComponentFixture<PostTypesAverageViewsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PostTypesAverageViewsComponent]
    });
    fixture = TestBed.createComponent(PostTypesAverageViewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
