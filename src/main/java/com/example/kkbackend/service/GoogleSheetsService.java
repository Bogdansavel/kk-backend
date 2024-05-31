package com.example.kkbackend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetAddress;
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
    private final String spreadsheetId = "1yF0Y_hsgmvg7ZjmqOf6QAnaz9SSRzM-3N4Xo70AHDlQ";
    private Sheets service;
    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(secret));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("online")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8889).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    public boolean cancel(String username) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
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
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
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
        final int sheetId = 1582377723;
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
