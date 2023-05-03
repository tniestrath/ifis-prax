import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageEinzelComponent } from './page-einzel.component';

describe('PageEinzelComponent', () => {
  let component: PageEinzelComponent;
  let fixture: ComponentFixture<PageEinzelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PageEinzelComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PageEinzelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
