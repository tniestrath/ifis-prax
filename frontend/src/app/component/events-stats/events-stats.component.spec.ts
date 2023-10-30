import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsletterStatsComponent } from './events-stats.component';

describe('NewsletterStatsComponent', () => {
  let component: NewsletterStatsComponent;
  let fixture: ComponentFixture<NewsletterStatsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewsletterStatsComponent]
    });
    fixture = TestBed.createComponent(NewsletterStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
