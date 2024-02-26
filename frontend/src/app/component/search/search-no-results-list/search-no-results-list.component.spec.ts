import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchNoResultsListComponent } from './search-no-results-list.component';

describe('SearchNoResultsListComponent', () => {
  let component: SearchNoResultsListComponent;
  let fixture: ComponentFixture<SearchNoResultsListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchNoResultsListComponent]
    });
    fixture = TestBed.createComponent(SearchNoResultsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
