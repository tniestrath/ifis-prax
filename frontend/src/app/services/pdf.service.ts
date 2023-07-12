import { Injectable } from '@angular/core';
import { jsPDF } from "jspdf";
import html2canvas from "html2canvas";
import {Chart} from "chart.js/auto";
@Injectable({
  providedIn: 'root'
})
export class PdfService {

  constructor() {
  }

  exportAsPDF(chart : Chart) {
    // @ts-ignore
    html2canvas(document.querySelector<HTMLCanvasElement>("#c_clicks"))
      .then(canvas => {
        console.log(chart)
        var doc = new jsPDF('p', 'pt', [canvas.width, canvas.height]);
        const contentDataURL = chart.canvas.toDataURL('image/png');
        doc.addImage(contentDataURL, 'PNG', 0, 0, canvas.width, canvas.height);
        doc.save('Filename.pdf');
      })
  }
}
