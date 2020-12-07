package com.itextpdf.jumpstart;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;

public class Main {
    public static void main(String[] args) throws IOException {

        // Create the output file
        File file = new File("output.pdf");

        // Open PDF document in write mode
        PdfWriter pdfWriter = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        // Create document to add new elements
        Document document = new Document(pdfDocument);
        document.add(new Paragraph("")); //This line turns out to be needed to actually create the doc.

        // Load the input.txt file
        FileInputStream fis = new FileInputStream("src/main/java/com/itextpdf/jumpstart/input.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        String content;
        List<String> list = new ArrayList<String>();
        while((content = in.readLine()) != null){
            list.add(content);
        }
        // All the lines from the input txt go into an array line-by-line
        String[] stringArr = list.toArray(new String[0]);

        // Prepare helper vars for processing the input
        String line;
        Text text = new Text("");
        Paragraph paragraph1 = new Paragraph("");
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont italicsFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        PdfFont currentFont = regularFont;
        int fontSize = 12;
        int indent = 0;

        // Process the input text
        for(int i=0; i < stringArr.length; i++){
            line = stringArr[i];

            // If statement checks if line is a command or text, if command, applies appropriate formatting
            if(line.startsWith(".")){
                if(line.equals(".normal")){
                    currentFont = regularFont;
                    fontSize = 12;
                }else if(line.equals(".large")){
                    currentFont = boldFont;
                    fontSize = 36;
                }else if(line.equals(".italics")){
                    currentFont = italicsFont;
                }else if(line.equals(".regular")){
                    currentFont = regularFont;
                }else if(line.equals(".bold")){
                    currentFont = boldFont;
                }else if(line.contains("indent")){
                    // setIndentation was removed from itext so using margins in a table's cells
                    if(line.charAt(8)==('+')){
                        indent += (int)line.charAt(9);
                        paragraph1.setMarginLeft(indent);
                    }else if(line.charAt(8)==('-')){
                        indent -= (int)line.charAt(9);
                        paragraph1.setMarginLeft(indent);
                    }
                }else if(line.equals(".fill")){
                    paragraph1.setTextAlignment(TextAlignment.JUSTIFIED_ALL);
                }else if(line.equals(".nofill")){
                    paragraph1.setTextAlignment(TextAlignment.LEFT);
                    //nofill submits paragraph to document
                    document.add(new Table(1).addCell(new Cell().setBorder(Border.NO_BORDER).add(paragraph1)));
                    paragraph1 = new Paragraph();
                }else if(line.equals(".paragraph")){
                    // paragraph command submits paragraph to the doc in a table/cell
                    document.add(new Table(1).addCell(new Cell().setBorder(Border.NO_BORDER).add(paragraph1)));
                    paragraph1 = new Paragraph(); // reset paragraph as itext doesnt seem to auto do it
                }
            }else{
                text.setText(" "+stringArr[i]); // adding extra space at the start of text lines for correct formatting

                // itext doesnt do deep copy for adding text obj to parag obj so this line effectively accomplishes that
                // this helps maintain changing formatting within the same paragraph
                paragraph1.add(new Text(text.getText()).setFont(currentFont).setFontSize(fontSize));
                text = new Text(""); // reset text object
            }
        }

        // submit the paragraph one last time to doc, worst case scenario parag obj is empty
        document.add(new Table(1).addCell(new Cell().setBorder(Border.NO_BORDER).add(paragraph1)));
        // Close document
        document.close();
    }
}
