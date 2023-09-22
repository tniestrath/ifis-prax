import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemloadComponent } from './systemload.component';

describe('SystemloadComponent', () => {
  let component: SystemloadComponent;
  let fixture: ComponentFixture<SystemloadComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SystemloadComponent]
    });
    fixture = TestBed.createComponent(SystemloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
