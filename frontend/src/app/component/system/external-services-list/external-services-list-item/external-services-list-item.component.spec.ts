import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExternalServicesListItemComponent } from './external-services-list-item.component';

describe('ExternalServicesListItemComponent', () => {
  let component: ExternalServicesListItemComponent;
  let fixture: ComponentFixture<ExternalServicesListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExternalServicesListItemComponent]
    });
    fixture = TestBed.createComponent(ExternalServicesListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
