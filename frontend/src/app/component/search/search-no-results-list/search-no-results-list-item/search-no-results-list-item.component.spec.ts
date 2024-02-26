import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchNoResultsListItemComponent } from './search-no-results-list-item.component';

describe('SearchNoResultsListItemComponent', () => {
  let component: SearchNoResultsListItemComponent;
  let fixture: ComponentFixture<SearchNoResultsListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchNoResultsListItemComponent]
    });
    fixture = TestBed.createComponent(SearchNoResultsListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
