import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchListItemComponent } from './search-list-item.component';

describe('SearchNoResultsListItemComponent', () => {
  let component: SearchListItemComponent;
  let fixture: ComponentFixture<SearchListItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchListItemComponent]
    });
    fixture = TestBed.createComponent(SearchListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
