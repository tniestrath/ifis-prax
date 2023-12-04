import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagChartComponent } from './tag-chart.component';

describe('TagChartComponent', () => {
  let component: TagChartComponent;
  let fixture: ComponentFixture<TagChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TagChartComponent]
    });
    fixture = TestBed.createComponent(TagChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
