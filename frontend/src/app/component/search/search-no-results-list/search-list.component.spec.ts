import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchListComponent } from './search-list.component';

describe('SearchNoResultsListComponent', () => {
  let component: SearchListComponent;
  let fixture: ComponentFixture<SearchListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchListComponent]
    });
    fixture = TestBed.createComponent(SearchListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
