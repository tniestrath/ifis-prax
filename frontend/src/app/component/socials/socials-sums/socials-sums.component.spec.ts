import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialsSumsComponent } from './socials-sums.component';

describe('SocialsSumsComponent', () => {
  let component: SocialsSumsComponent;
  let fixture: ComponentFixture<SocialsSumsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SocialsSumsComponent]
    });
    fixture = TestBed.createComponent(SocialsSumsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
