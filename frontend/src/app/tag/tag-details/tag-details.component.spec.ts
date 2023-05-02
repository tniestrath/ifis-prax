import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagDetailsComponent } from './tag-details.component';

describe('TagDetailsComponent', () => {
  let component: TagDetailsComponent;
  let fixture: ComponentFixture<TagDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TagDetailsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
