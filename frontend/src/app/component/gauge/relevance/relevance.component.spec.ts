import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelevanceComponent } from './relevance.component';

describe('RelevanceComponent', () => {
  let component: RelevanceComponent;
  let fixture: ComponentFixture<RelevanceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelevanceComponent]
    });
    fixture = TestBed.createComponent(RelevanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
