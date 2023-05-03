import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {DbService} from "../../services/db.service";
import {Tag} from "../Tag";

@Component({
  selector: 'dash-tag-lister',
  templateUrl: './tag-lister.component.html',
  styleUrls: ['./tag-lister.component.css']
})
export class TagListerComponent implements OnInit, OnChanges{
   tags: Tag[] = [];
   allTags : Tag[] = [];
  @Input() searchValue : string = "";
  @Output() selectedTag = new EventEmitter<string>();

  constructor(private db : DbService) {
  }

  onClick(value: Tag) {
    let val  = value.name.slice(0, 35);
    this.selectedTag.emit(val);
  }

  ngOnInit(): void {
    this.db.getAllTags().then(res =>  this.allTags = res)
    this.tags = this.allTags;
  }
  ngOnChanges(changes: SimpleChanges): void {

    this.tags = this.allTags.filter(
      value => value.name.toUpperCase().includes(this.searchValue.toUpperCase()));
  }
}
