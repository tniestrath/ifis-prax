import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";

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
      document.getElementsByClassName("user-stats-by-plan-box")[0].classList.remove("hidden");
      document.getElementsByClassName("user-stats-by-plan-box")[1].classList.remove("hidden");
      document.getElementsByClassName("user-tags-dist-box")[0].classList.remove("hidden");
    } else {
      // @ts-ignore
      document.getElementById("user-list2").classList.remove("hidden");
      // @ts-ignore
      document.getElementById("searchbar2").classList.remove("hidden");
      document.getElementsByClassName("user-stats-by-plan-box")[0].classList.add("hidden");
      document.getElementsByClassName("user-stats-by-plan-box")[1].classList.add("hidden");
      document.getElementsByClassName("user-tags-dist-box")[0].classList.add("hidden");
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
      document.getElementsByClassName("user-stats-by-plan-box")[0].classList.remove("hidden");
      document.getElementsByClassName("user-stats-by-plan-box")[1].classList.remove("hidden");
      document.getElementsByClassName("user-tags-dist-box")[0].classList.remove("hidden");
    } else {
      // @ts-ignore
      document.getElementById("user-list").classList.remove("user-list-extended");
      // @ts-ignore
      document.getElementById("user-list2").classList.remove("hidden");
      // @ts-ignore
      document.getElementById("searchbar2").classList.remove("hidden");
      document.getElementsByClassName("user-stats-by-plan-box")[0].classList.add("hidden");
      document.getElementsByClassName("user-stats-by-plan-box")[1].classList.add("hidden");
      document.getElementsByClassName("user-tags-dist-box")[0].classList.add("hidden");
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

    document.getElementsByClassName("user-stats-by-plan-box")[0].classList.remove("hidden");
    document.getElementsByClassName("user-stats-by-plan-box")[1].classList.remove("hidden");
    document.getElementsByClassName("user-tags-dist-box")[0].classList.remove("hidden");
  }

}