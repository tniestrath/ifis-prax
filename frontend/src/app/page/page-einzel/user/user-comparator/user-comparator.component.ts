import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";

@Component({
  selector: 'dash-user-comparator',
  templateUrl: './user-comparator.component.html',
  styleUrls: ['./user-comparator.component.css']
})
export class UserComparatorComponent extends DashBaseComponent implements OnInit{
  isComparing : boolean = false;

  ngOnInit(): void {
    if (!this.isComparing){
      // @ts-ignore
      document.getElementById("user-list").classList.add("user-list-extended");
      // @ts-ignore
      document.getElementById("user-list2").classList.add("hidden");
      // @ts-ignore
      document.getElementById("searchbar2").classList.add("hidden");
    } else {
      // @ts-ignore
      document.getElementById("user-list2").classList.remove("hidden");
      // @ts-ignore
      document.getElementById("searchbar2").classList.remove("hidden");
    }
  }


  onCompareClick() {
    if (this.isComparing){
      // @ts-ignore
      document.getElementById("user-list").classList.add("user-list-extended");
      // @ts-ignore
      document.getElementById("user-list2").classList.add("hidden");
      // @ts-ignore
      document.getElementById("searchbar2").classList.add("hidden");
    } else {
      // @ts-ignore
      document.getElementById("user-list").classList.remove("user-list-extended");
      // @ts-ignore
      document.getElementById("user-list2").classList.remove("hidden");
      // @ts-ignore
      document.getElementById("searchbar2").classList.remove("hidden");
    }
    this.isComparing = !this.isComparing;
  }
  onCancelCompareClick() {
    this.isComparing = false;
    // @ts-ignore
    document.getElementById("user-list").classList.add("user-list-extended");
    // @ts-ignore
    document.getElementById("user-list2").classList.add("hidden");
    // @ts-ignore
    document.getElementById("searchbar2").classList.add("hidden");
  }

}
