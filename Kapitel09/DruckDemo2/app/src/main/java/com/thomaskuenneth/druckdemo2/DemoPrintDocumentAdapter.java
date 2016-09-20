package com.thomaskuenneth.druckdemo2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import java.io.FileOutputStream;
import java.io.IOException;

class DemoPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private PrintedPdfDocument pdf;
    private int numPages;

    DemoPrintDocumentAdapter(Context context) {
        this.context = context;
    }

    @Override // muss implementiert werden
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle extras) {
        // sofern vorhanden, altes freigeben
        disposePdf();
        // neues PDF-Dokument mit den gewünschten Attributen erzeugen
        pdf = new PrintedPdfDocument(context, newAttributes);
        // auf Abbruchwunsch reagieren
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            disposePdf();
            return;
        }
        // erwartete Seitenzahl berechnen
        numPages = computePageCount(newAttributes);
        if (numPages > 0) {
            // Informationen an das Print-Framework zurückliefern
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder("sin_cos.pdf")
                    .setContentType(
                            PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(numPages)
                    .build();
            callback.onLayoutFinished(info,
                    !newAttributes.equals(oldAttributes));
        } else {
            // einen Fehler melden
            callback.onLayoutFailed(
                    "Fehler beim Berechnen der Seitenzahl");
        }
    }

    @Override // muss implementiert werden
    public void onWrite(PageRange[] pages,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        // über ale Seiten des Dokuments iterieren
        for (int i = 0; i < numPages; i++) {
            // Abbruch?
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                disposePdf();
                return;
            }
            PdfDocument.Page page = pdf.startPage(i);
            drawPage(page);
            pdf.finishPage(page);
        }
        // PDF-Dokument schreiben
        try {
            pdf.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        }
        callback.onWriteFinished(pages);
    }

    @Override
    public void onFinish() {
        disposePdf();
    }

    private int computePageCount(PrintAttributes printAttributes) {
        PrintAttributes.MediaSize size = printAttributes.getMediaSize();
        return (size == null) || !size.isPortrait()
                ? 2 : 1;
    }

    // Einheiten entsprechen 1/72 Zoll
    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();
        int nr = page.getInfo().getPageNumber();
        // Breite und Höhe
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        // Mittelpunkt
        int cx = w / 2;
        int cy = h / 2;
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLUE);
        canvas.drawLine(cx, 0, cx, h - 1, paint);
        canvas.drawLine(0, cy, w - 1, cy, paint);
        paint.setColor(Color.BLACK);
        for (int i = 0; i < w; i++) {
            int y;
            if (nr == 0) {
                y = (int) (Math.sin(i * ((2 * Math.PI) / w)) * cy + cy);
            } else {
                y = (int) (Math.cos(i * ((2 * Math.PI) / w)) * cy + cy);
            }
            canvas.drawPoint(i, y, paint);
        }
    }

    private void disposePdf() {
        if (pdf != null) {
            pdf.close();
            pdf = null;
        }
    }
}
