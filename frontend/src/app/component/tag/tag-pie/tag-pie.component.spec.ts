import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagPieComponent } from './tag-pie.component';

describe('TagPieComponent', () => {
  let component: TagPieComponent;
  let fixture: ComponentFixture<TagPieComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TagPieComponent]
    });
    fixture = TestBed.createComponent(TagPieComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
