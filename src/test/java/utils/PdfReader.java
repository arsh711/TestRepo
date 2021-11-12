package utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.FileInputStream;
import java.io.IOException;

public class PdfReader {
    public static String getTextFromPdf(String fileName) throws IOException {
    FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir")+PropertyUtils.getProperty("default.download.location") + "/" + fileName);
    PDDocument document = PDDocument.load(inputStream);
    inputStream.close();
    return new PDFTextStripper().getText(document);
}
}
