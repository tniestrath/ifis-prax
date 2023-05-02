import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css']
})
export class SearchbarComponent {

  @Output() searchInput :string = "";

  @Input() selectedSearch : string = "";
  displaySearchBox: string = "";

  onKey(value : string) {
    this.searchInput = value;
  }

  onTagSelected(tag : string){
    if (tag != ""){
      this.selectedSearch = tag;
      this.displaySearchBox = "0";
    }
  }

  onCancelClick(){
    this.selectedSearch = "";
    this.displaySearchBox = "50px";
  }
}
