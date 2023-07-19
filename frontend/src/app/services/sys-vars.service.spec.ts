import { TestBed } from '@angular/core/testing';

import { SysVars } from './sys-vars-service';

describe('UserService', () => {
  let service: SysVars;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SysVars);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
