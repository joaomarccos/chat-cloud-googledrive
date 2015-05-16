package br.edu.ifpb.pod.app.chat.file;

import br.edu.ifpb.pod.app.chat.file.entitys.User;
import br.edu.ifpb.pod.exemplodriveapi.DriveController;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Marcos F <joaomarccos.ads@gmail.com>
 */
public class LoginController {

    private static final Path PATH = Paths.get("src/main/resources/login.txt");   
    private long lastFileLenght;
    private ListenerChangeFile listenner;
    private DriveController driveController;
    
    
    public LoginController(DriveController driveController) throws IOException {                                               
        this.driveController = driveController;
        this.lastFileLenght = getFileSize();
    }

    @SuppressWarnings("empty-statement")
    public boolean login(User user) throws IOException {

        ArrayList<User> loggedUsers = loadUsers();

        if (!loggedUsers.contains(user)) {
            registerOnCloud(user);
            return true;
        }
        
        return false;

    }

    private boolean registerOnCloud(User user) throws IOException {     

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(PATH, CREATE, java.nio.file.StandardOpenOption.APPEND))) {
            byte[] bytes = (user.getName() + "\n").getBytes();
            out.write(bytes, 0, bytes.length);
        }       
        
        updateCloudFile();
        
        return true;

    }

    private void updateCloudFile() throws IOException {
        File file = driveController.getFile(DriveController.LOGIN_DOC_ID);
        FileContent mediaContent = new FileContent("text/plain", PATH.toFile());
        driveController.updateFile(file, mediaContent);
    }

    private ArrayList<User> loadUsers() throws IOException {
        ArrayList<User> users = new ArrayList<>();

        updateLocalFile();
        
        try (BufferedReader read = Files.newBufferedReader(PATH)) {
            String line = null;
            while ((line = read.readLine()) != null) {
                users.add(new User(line));
            }
        }

        return users;
    }

    private void updateLocalFile() throws IOException, FileNotFoundException {
        FileOutputStream out = new FileOutputStream(PATH.toFile());
        InputStream in = driveController.obtainFile(DriveController.LOGIN_DOC_ID);
        byte[] b = new byte[1];
        while(in.read(b)!=-1){
            out.write(b);
        }
    }

    @SuppressWarnings("empty-statement")
    public void logout(User user) throws IOException {

        ArrayList<User> loggedUsers = loadUsers();
        loggedUsers.remove(user);                       

        StringBuilder sb = new StringBuilder();
        for (User loggedUser : loggedUsers) {
            sb.append(loggedUser.getName()).append("\n");
        }
        Files.write(PATH, sb.toString().getBytes());        
        
        updateCloudFile();
        
    }    
    
    public void addListenner(ListenerChangeFile listenning) {
        this.listenner = listenning;
    }        

    public ArrayList<User> listUsers() throws IOException {
        return loadUsers();
    }
    
    private long getFileSize() throws IOException{
        return driveController.getFile(DriveController.LOGIN_DOC_ID).getFileSize();
    }
    
    public void checkUsers() {        
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {       
                    long lenghtNow = lastFileLenght;
                    try {
                        if((lastFileLenght = getFileSize())!=lenghtNow)
                           listenner.notifyChangeOnLogin();
                    } catch (IOException ex) {
                        Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

}
