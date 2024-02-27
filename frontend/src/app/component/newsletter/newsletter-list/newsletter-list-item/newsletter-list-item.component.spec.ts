import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsletterListItemComponent } from './newsletter-list-item.component';

describe('NewsletterListItemComponent', () => {
  let component: NewsletterListItemComponent;
  let fixture: ComponentFixture<NewsletterListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewsletterListItemComponent]
    });
    fixture = TestBed.createComponent(NewsletterListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
