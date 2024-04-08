import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BadBotItemComponent } from './bad-bot-item.component';

describe('BadBotItemComponent', () => {
  let component: BadBotItemComponent;
  let fixture: ComponentFixture<BadBotItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BadBotItemComponent]
    });
    fixture = TestBed.createComponent(BadBotItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
