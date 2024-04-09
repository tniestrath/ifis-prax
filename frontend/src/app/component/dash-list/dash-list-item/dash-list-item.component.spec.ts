import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashListItemComponent } from './dash-list-item.component';

describe('DashListItemComponent', () => {
  let component: DashListItemComponent;
  let fixture: ComponentFixture<DashListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DashListItemComponent]
    });
    fixture = TestBed.createComponent(DashListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
