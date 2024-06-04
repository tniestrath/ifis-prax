import {Component, OnInit} from '@angular/core';
import {DashListComponent, DashListPageableComponent} from "../../dash-list/dash-list.component";
import {DbObject} from "../../../services/DbObject";
import {ExternalServicesListItemComponent} from "./external-services-list-item/external-services-list-item.component";

export class ExternalService extends DbObject{
  public link : string;
  public check : number;
  constructor(id : string, name : string, link : string, check : number) {
    super(id, name);
    this.link = link;
    this.check = check;
  }
}

@Component({
  selector: 'dash-external-services-list',
  templateUrl: './external-services-list.component.html',
  styleUrls: ['./external-services-list.component.css']
})
export class ExternalServicesListComponent extends DashListPageableComponent<ExternalService, ExternalServicesListItemComponent>{

  override ngOnInit() {
    this.load(this.api.getServices(0, this.pageSize), ExternalServicesListItemComponent);
  }


}
