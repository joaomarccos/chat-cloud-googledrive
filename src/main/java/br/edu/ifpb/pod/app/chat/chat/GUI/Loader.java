package br.edu.ifpb.pod.app.chat.chat.GUI;

import br.edu.ifpb.pod.exemplodriveapi.DriveController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Marcos F <joaomarccos.ads@gmail.com>
 */
public class Loader {

    public static void main(String[] args) throws IOException {

        try {
            DriveController dc = new DriveController();
            MainFrame main = new MainFrame(dc);
            main.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
