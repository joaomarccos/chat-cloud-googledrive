package br.edu.ifpb.pod.app.chat.file;

import br.edu.ifpb.pod.app.chat.file.entitys.Message;
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
public class MessageController {

    private static final Path PATH = Paths.get("src/main/resources/mensagens.txt");
    private long lastFileLenght;
    private DriveController driveController;
    private ListenerChangeFile listenner;

    public MessageController(DriveController dc) throws IOException {        
        this.driveController = dc;
        this.lastFileLenght = getFileSize();
    }

    @SuppressWarnings("empty-statement")
    public void sendMessage(Message msg) throws IOException {
        registerOnCloud(msg);
    }

    private void registerOnCloud(Message msg) throws IOException {
        
        updateLocalFile();

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(PATH, CREATE, java.nio.file.StandardOpenOption.APPEND))) {
            byte[] bytes = (msg + "\n").getBytes();
            out.write(bytes, 0, bytes.length);
        }

        updateCloudFile();        

    }

    private void updateCloudFile() throws IOException {
        File file = driveController.getFile(DriveController.MENSAGEM_DOC_ID);
        FileContent mediaContent = new FileContent("text/plain", PATH.toFile());
        driveController.updateFile(file, mediaContent);
    }

    private void updateLocalFile() throws IOException, FileNotFoundException {
        FileOutputStream out = new FileOutputStream(PATH.toFile());
        InputStream in = driveController.obtainFile(DriveController.MENSAGEM_DOC_ID);
        byte[] b = new byte[1];
        while (in.read(b) != -1) {
            out.write(b);
        }
    }

    private ArrayList<Message> loadMessages() throws NumberFormatException, IOException {

        ArrayList<Message> msgs = new ArrayList<>();
        
        updateLocalFile();

        try (BufferedReader read = Files.newBufferedReader(PATH)) {
            String line;
            String[] data;
            while ((line = read.readLine()) != null) {
                data = line.split(":");
                msgs.add(new Message(Long.parseLong(data[0]), data[1], data[2]));
            }
        }
        
        return msgs;
    }

    public ArrayList<Message> listMessages() throws NumberFormatException, IOException {
        return loadMessages();
    }

    public void addListenner(ListenerChangeFile listenning) {
        this.listenner = listenning;
    }    
    
    private long getFileSize() throws IOException{
        return driveController.getFile(DriveController.MENSAGEM_DOC_ID).getFileSize();
    }

    public void checkMessages() {
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {       
                    long lenghtNow = lastFileLenght;
                    try {
                        if((lastFileLenght = getFileSize())!=lenghtNow)
                            listenner.notifyChangeOnMessages();
                    } catch (IOException ex) {
                        Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        new Thread(runnable).start();
    }
}
