import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GaugeComponent } from './gauge.component';

describe('PodiumComponent', () => {
  let component: GaugeComponent;
  let fixture: ComponentFixture<GaugeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GaugeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GaugeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
