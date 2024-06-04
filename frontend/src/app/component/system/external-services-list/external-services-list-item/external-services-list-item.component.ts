import {Component, OnInit} from '@angular/core';
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";
import {ExternalService} from "../external-services-list.component";

@Component({
  selector: 'dash-external-services-list-item',
  templateUrl: './external-services-list-item.component.html',
  styleUrls: ['./external-services-list-item.component.css']
})
export class ExternalServicesListItemComponent extends DashListItemComponent implements OnInit{
  override data : ExternalService = new ExternalService("", "", "" , 2);

  ngOnInit(): void {
    fetch(this.data.link).then(response => {
      if (response.status >= 200 && response.status <= 299){
        return this.data.check = 0;
      } else if (response.status >= 300 && response.status <= 399){
        return this.data.check = 1;
      } else {
        return this.data.check = 2;
      }
    })
  }

}
