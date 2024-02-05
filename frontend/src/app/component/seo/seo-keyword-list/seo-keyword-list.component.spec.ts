import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeoKeywordListComponent } from './seo-keyword-list.component';

describe('SeoKeywordListComponent', () => {
  let component: SeoKeywordListComponent;
  let fixture: ComponentFixture<SeoKeywordListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeoKeywordListComponent]
    });
    fixture = TestBed.createComponent(SeoKeywordListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
