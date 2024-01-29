import {Injectable} from '@angular/core';
import {jsPDF} from "jspdf";
import html2canvas from "html2canvas";
import {Chart} from "chart.js/auto";
import {style} from "@angular/animations";
@Injectable({
  providedIn: 'root'
})
export class PdfService {

  constructor() {
  }

  exportCahrtAsPDF(chart : Chart) {
    // @ts-ignore
    html2canvas(document.querySelector<HTMLCanvasElement>("#c_clicks"))
      .then(canvas => {
        console.log(chart)
        var doc = new jsPDF('p', 'pt', [canvas.width, canvas.height]);
        const contentDataURL = chart.canvas.toDataURL('image/png');
        doc.addImage(contentDataURL, 'PNG', 0, 0, canvas.width, canvas.height);
        doc.save('Filename.pdf');
      });
  }


  savePdf(element : HTMLElement) {
    console.log(element)
    var data = element;
    html2canvas(data).then(canvas => {
      // Few necessary setting options
      var imgWidth = 208;
      var pageHeight = 295;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      var heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png')
      let pdf = new jsPDF('l', 'mm', 'a4'); // A4 size page of PDF
      var position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight)
      pdf.save('MYPdf.pdf'); // Generated PDF
    });

  }
}
