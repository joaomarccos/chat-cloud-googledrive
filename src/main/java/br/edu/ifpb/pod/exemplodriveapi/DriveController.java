package br.edu.ifpb.pod.exemplodriveapi;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author João Marcos F <joaomarccos.ads@gmail.com>
 */
public class DriveController {

    private static final String CLIENT_ID = "31931409646-f5f54022vuehcrljt0vp18k1lrn5d6bc.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "fRPeMOzFRRjhtHFgWGPOHiQy";

    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private Drive service;

    public static final String MENSAGEM_DOC_ID = "0B4qmSotq4S_qX1BwNXpTY2V3WkU";
    public static final String LOGIN_DOC_ID = "0B4qmSotq4S_qMjh2bzM2UktTZGs";

    public DriveController() throws IOException {
        this.service = processIncialize();
    }

    public void updateFile(File file, FileContent mediaContent) throws IOException {
        service.files().update(file.getId(), file, mediaContent).execute();
    }

    public InputStream obtainFile(String docId) throws IOException {
        return service.files().get(docId).executeMediaAsInputStream();                
    }       
    
    public File getFile(String id) throws IOException{
        return service.files().get(id).execute();
    }

    private static Drive processIncialize() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        String code = confirmAuth(url);
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
        //Create a new authorized API client
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        return service;
    }

    public static String confirmAuth(String url) throws IOException {
        String code = JOptionPane.showInputDialog("Por favor abra a url a seguir no seu navegador e cole o codigo de autenticaçao: ",url);        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));        
        return code;
    }

}
