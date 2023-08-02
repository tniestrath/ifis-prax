import { Component } from '@angular/core';
import {TagRanking} from "../../Tag";

@Component({
  selector: 'dash-tag-list-item',
  templateUrl: './tag-list-item.component.html',
  styleUrls: ['./tag-list-item.component.css']
})
export class TagListItemComponent {
  data : TagRanking = new TagRanking("" ,"","", "","");
  protected readonly parseFloat = parseFloat;
}
