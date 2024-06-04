package com.example.kkbackend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleSheetsService {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    @Value("${spring.application.secret.google-sheets}")
    private String secret;
    private final String spreadsheetId = "1C-eAcBjMMnaTkbrRUdnNMss1u0k1tfv_QjOrH68D83E";
    private Sheets service;
    public Credential getCredentials() throws IOException {
        return GoogleCredential.fromStream(new ByteArrayInputStream(secret.getBytes(StandardCharsets.UTF_8)))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    }


    public boolean cancel(String username) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        List<String> ranges = List.of("G2");
        BatchGetValuesResponse readResult = service.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute();
        String date = (String) readResult.getValueRanges().get(0).getValues().get(0).get(0);

        ranges = List.of("A2:E");
        readResult = service.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute();
        int i = 2;
        for (List<Object> value : readResult.getValueRanges().get(0).getValues()) {
            if ((value.get(0)).equals(date)
            && (value.get(4)).equals(username)) {
                Color redColor = new Color();
                redColor.setRed(14f);
                redColor.setGreen(104f);
                redColor.setBlue(104f);
                paintRow(i, redColor);
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean revert(String username) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<String> ranges = List.of("G2");
        BatchGetValuesResponse readResult = service.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute();
        String date = (String) readResult.getValueRanges().get(0).getValues().get(0).get(0);

        ranges = List.of("A2:E");
        readResult = service.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute();
        int i = 2;
        for (List<Object> value : readResult.getValueRanges().get(0).getValues()) {
            if ((value.get(0)).equals(date)
                    && (value.get(4)).equals(username)) {
                Color whiteColor = new Color();
                paintRow(i, whiteColor);
                return true;
            }
            i++;
        }
        return false;
    }

    private void paintRow(int rowNumber, Color color) throws IOException {
        final int sheetId = 499746787;
        CellFormat cellFormat = new CellFormat(); //setting cell color

        GridRange gridRange = new GridRange(); //setting grid that we will paint
        gridRange.setSheetId(sheetId); //you can find it in your URL - param "gid"
        gridRange.setStartRowIndex(rowNumber-1);
        gridRange.setEndRowIndex(rowNumber);
        gridRange.setStartColumnIndex(0);
        gridRange.setEndColumnIndex(6);
        cellFormat.setBackgroundColor(color);

        CellData cellData = new CellData();
        cellData.setUserEnteredFormat(cellFormat);

        List<Request> requestList = new ArrayList<>();
        requestList.add(new Request().setRepeatCell(new RepeatCellRequest().setCell(cellData)
                .setRange(gridRange).setFields("userEnteredFormat.backgroundColor")));

        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
        batchUpdateSpreadsheetRequest.setRequests(requestList);

        final Sheets.Spreadsheets.BatchUpdate batchUpdate = service.
                spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);

        batchUpdate.execute();
    }
}
