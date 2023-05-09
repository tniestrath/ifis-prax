import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashBaseComponent } from './dash-base.component';

describe('DashBaseComponent', () => {
  let component: DashBaseComponent;
  let fixture: ComponentFixture<DashBaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashBaseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
