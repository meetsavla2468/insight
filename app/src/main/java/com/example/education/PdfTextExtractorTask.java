package com.example.education;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PdfTextExtractorTask extends AsyncTask<String, Void, String> {

    private final Context context;
    private final pdf.PdfExtractionCallback callback;  // Add callback

    // Constructor to pass context and callback
    public PdfTextExtractorTask(Context context, pdf.PdfExtractionCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {
        String pdfUrl = urls[0];
        File pdfFile = downloadPdf(pdfUrl);

        if (pdfFile != null && pdfFile.exists()) {
            return extractTextFromPdf(pdfFile);
        } else {
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (!result.isEmpty()) {
            Log.d("Extracted Text", result);
            callback.onPdfTextExtracted(result);  // Call the callback with the extracted text
        } else {
            Toast.makeText(context, "Failed to extract text from PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    private File downloadPdf(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download file: " + response);
            }

            InputStream inputStream = response.body().byteStream();
            File pdfFile = new File(context.getCacheDir(), "temp_pdf_file.pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return pdfFile;
        } catch (IOException e) {
            Log.e("Download PDF", "Error downloading PDF", e);
            return null;
        }
    }

    private String extractTextFromPdf(File pdfFile) {
        StringBuilder extractedText = new StringBuilder();

        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFile.getAbsolutePath()));
            int totalPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= totalPages; i++) {
                extractedText.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
            }
            pdfDoc.close();
        } catch (IOException e) {
            Log.e("Extract Text", "Error reading PDF", e);
        }

        return extractedText.toString();
    }
}
