package com.example.education;

import org.json.JSONException;

public interface PdfExtractionCallback {
    void onPdfTextExtracted(String extractedText) throws JSONException;
}
