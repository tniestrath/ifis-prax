import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumProfanityFilterAdderComponent } from './forum-profanity-filter-adder.component';

describe('ForumProfanityFilterAdderComponent', () => {
  let component: ForumProfanityFilterAdderComponent;
  let fixture: ComponentFixture<ForumProfanityFilterAdderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ForumProfanityFilterAdderComponent]
    });
    fixture = TestBed.createComponent(ForumProfanityFilterAdderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
