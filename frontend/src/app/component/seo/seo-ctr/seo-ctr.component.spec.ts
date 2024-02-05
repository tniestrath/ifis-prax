import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeoCtrComponent } from './seo-ctr.component';

describe('SeoCtrComponent', () => {
  let component: SeoCtrComponent;
  let fixture: ComponentFixture<SeoCtrComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeoCtrComponent]
    });
    fixture = TestBed.createComponent(SeoCtrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
