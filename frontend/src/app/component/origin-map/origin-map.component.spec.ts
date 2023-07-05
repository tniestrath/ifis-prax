import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OriginMapComponent } from './origin-map.component';

describe('OriginMapComponent', () => {
  let component: OriginMapComponent;
  let fixture: ComponentFixture<OriginMapComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OriginMapComponent]
    });
    fixture = TestBed.createComponent(OriginMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
