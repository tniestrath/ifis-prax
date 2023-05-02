import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyListerComponent } from './company-lister.component';

describe('CompanyListerComponent', () => {
  let component: CompanyListerComponent;
  let fixture: ComponentFixture<CompanyListerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompanyListerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyListerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
