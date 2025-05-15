package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Cart;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateOrdersPdf(List<Orders> orders, String farmerName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Orders Report for " + farmerName, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Create table for orders
            PdfPTable table = new PdfPTable(5); // 7 columns
            table.setWidthPercentage(100);
            
            // Set column widths
            float[] columnWidths = {1f, 2f, 2f, 2f, 2f};
            table.setWidths(columnWidths);

            // Add table headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            table.addCell(new Phrase("Order ID", headerFont));
            table.addCell(new Phrase("Order Date", headerFont));
            table.addCell(new Phrase("Delivery Date", headerFont));
            table.addCell(new Phrase("Status", headerFont));
            table.addCell(new Phrase("Total Items", headerFont));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            // Add order data
            for (Orders order : orders) {
                table.addCell(new Phrase(String.valueOf(order.getId()), cellFont));
                table.addCell(new Phrase(order.getOrderDate().format(formatter), cellFont));
                table.addCell(new Phrase(order.getDeliveryDate().format(formatter), cellFont));
                table.addCell(new Phrase(order.getOrderStatus().toString(), cellFont));
                table.addCell(new Phrase(String.valueOf(order.getCarts().size()), cellFont));
            }

            document.add(table);

            // Add summary
            document.add(new Paragraph("\n"));
            Paragraph summary = new Paragraph(String.format("Total Orders: %d", orders.size()));
            summary.setAlignment(Element.ALIGN_RIGHT);
            document.add(summary);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
} 