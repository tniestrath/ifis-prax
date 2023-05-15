import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClicksComponent } from './clicks.component';

describe('ClicksComponent', () => {
  let component: ClicksComponent;
  let fixture: ComponentFixture<ClicksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClicksComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClicksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
