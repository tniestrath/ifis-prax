import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagListerComponent } from './tag-lister.component';

describe('TagListerComponent', () => {
  let component: TagListerComponent;
  let fixture: ComponentFixture<TagListerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TagListerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagListerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
