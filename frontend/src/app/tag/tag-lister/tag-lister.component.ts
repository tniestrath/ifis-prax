import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'dash-tag-lister',
  templateUrl: './tag-lister.component.html',
  styleUrls: ['./tag-lister.component.css']
})
export class TagListerComponent implements OnInit, OnChanges{
   tags: string[] = [];
   allTags : string[] = [];
  @Input() searchValue : string = "";
  @Output() selectedTag = new EventEmitter<string>();

  onClick(value: string) {
    this.selectedTag.emit(value);
  }

  ngOnInit(): void {
    this.allTags = ["tag1", "tag2", "helloTag" , "someTag"];
    this.tags = this.allTags;
  }
  ngOnChanges(changes: SimpleChanges): void {

    this.tags = this.allTags.filter(
      value => value.toUpperCase().includes(this.searchValue.toUpperCase()));
  }
}
