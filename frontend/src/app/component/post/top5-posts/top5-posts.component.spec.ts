import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Top5PostsComponent } from './top5-posts.component';

describe('Top5PostsComponent', () => {
  let component: Top5PostsComponent;
  let fixture: ComponentFixture<Top5PostsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [Top5PostsComponent]
    });
    fixture = TestBed.createComponent(Top5PostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
