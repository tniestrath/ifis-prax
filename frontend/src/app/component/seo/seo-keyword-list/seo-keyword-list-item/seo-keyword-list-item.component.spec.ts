import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeoKeywordListItemComponent } from './seo-keyword-list-item.component';

describe('SeoKeywordListItemComponent', () => {
  let component: SeoKeywordListItemComponent;
  let fixture: ComponentFixture<SeoKeywordListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeoKeywordListItemComponent]
    });
    fixture = TestBed.createComponent(SeoKeywordListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
