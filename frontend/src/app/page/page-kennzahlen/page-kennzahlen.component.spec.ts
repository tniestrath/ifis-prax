import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageKennzahlenComponent } from './page-kennzahlen.component';

describe('PageKennzahlenComponent', () => {
  let component: PageKennzahlenComponent;
  let fixture: ComponentFixture<PageKennzahlenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PageKennzahlenComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PageKennzahlenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
