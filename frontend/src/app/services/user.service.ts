import {EventEmitter, Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public static USER_ID : string = "0";
  public static SELECTED_POST_ID : EventEmitter<number> = new EventEmitter<number>();
  constructor() {}
}
