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
  @Output() selectedTag = new EventEmitter<Tag>();

  constructor(private db : DbService) {
  }

  onClick(value: Tag) {
    this.selectedTag.emit(value);
  }

  ngOnInit(): void {
    this.db.loadAllTags().then(() =>  this.allTags = DbService.Tags).then(() => this.tags = this.allTags);
  }
  ngOnChanges(changes: SimpleChanges): void {

    this.tags = this.allTags.filter(
      value => value.name.toUpperCase().includes(this.searchValue.toUpperCase()));
  }
}
