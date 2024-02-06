package com.file.service.Impl;


import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class DocFileMapper {

    public static byte[] convertHtmlToDocx(byte[] htmlData) throws IOException {
        try (XWPFDocument docxDocument = new XWPFDocument();) {
            String html = new String(htmlData, StandardCharsets.UTF_8);
            Document jsoupDoc = Jsoup.parse(html);
            jsoupDoc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
            Elements paragraphs = jsoupDoc.select("p");
            for (Element paragraph : paragraphs) {
                String text = paragraph.text();
                if (!text.isEmpty()) {
                    XWPFParagraph docxParagraph = docxDocument.createParagraph();
                    XWPFRun run = docxParagraph.createRun();
                    run.setText(text);
                }
            }
            Elements tables = jsoupDoc.select("table");
            for (Element table : tables) {
                XWPFTable docxTable = docxDocument.createTable();
                for (Element row : table.select("tr")) {
                    XWPFTableRow docxRow = docxTable.createRow();
                    for (Element cell : row.select("td")) {
                        XWPFTableCell docxCell = docxRow.createCell();
                        docxCell.setText(cell.text());
                    }
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            docxDocument.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static byte[] convertDocxToHtml(byte[] docxData) throws IOException {
        Document htmlDoc = Document.createShell("");
        Element body = htmlDoc.body();

        XWPFDocument docxDocument = new XWPFDocument(new ByteArrayInputStream(docxData));
        for (XWPFParagraph paragraph : docxDocument.getParagraphs()) {
            Element paragraphElement = body.appendElement("p");
            paragraphElement.text(paragraph.getText());
        }

        for (XWPFTable table : docxDocument.getTables()) {
            Element tableElement = body.appendElement("table");
            for (XWPFTableRow row : table.getRows()) {
                Element rowElement = tableElement.appendElement("tr");
                for (XWPFTableCell cell : row.getTableCells()) {
                    Element cellElement = rowElement.appendElement("td");
                    cellElement.text(cell.getText());
                }
            }
        }
        return htmlDoc.toString().getBytes(StandardCharsets.UTF_8);
    }
}
