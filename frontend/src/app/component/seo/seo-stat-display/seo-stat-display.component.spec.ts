import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeoStatDisplayComponent } from './seo-stat-display.component';

describe('SeoStatDisplayComponent', () => {
  let component: SeoStatDisplayComponent;
  let fixture: ComponentFixture<SeoStatDisplayComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeoStatDisplayComponent]
    });
    fixture = TestBed.createComponent(SeoStatDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
