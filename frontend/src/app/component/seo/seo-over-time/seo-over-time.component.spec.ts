import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeoOverTimeComponent } from './seo-over-time.component';

describe('SeoOverTimeComponent', () => {
  let component: SeoOverTimeComponent;
  let fixture: ComponentFixture<SeoOverTimeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeoOverTimeComponent]
    });
    fixture = TestBed.createComponent(SeoOverTimeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
