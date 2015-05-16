package br.edu.ifpb.pod.exemplodriveapi;


import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;

// ...

public class Downloader {

  // ...

  /**
   * Download a file's content.
   *
   * @param service Drive API service instance.
   * @param file Drive File instance.
   * @return InputStream containing the file's content if successful,
   *         {@code null} otherwise.
   */
  public static InputStream downloadFile(Drive service, File file) {
    if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
      try {
        // uses alt=media query parameter to request content
        return service.files().get(file.getId()).executeMediaAsInputStream();
      } catch (IOException e) {
        // An error occurred.
        e.printStackTrace();
        return null;
      }
    } else {
      // The file doesn't have any content stored on Drive.
      return null;
    }
  }

  // ...
//  InputStream in = Downloader.downloadFile(service, file);
//        ByteArrayOutputStream txt = new ByteArrayOutputStream();
//        byte[] b = new byte[1];
//        while (in.read(b) != -1) {
//            txt.write(b);
//        }
//
//        java.io.File newFile = new java.io.File("src/main/resources/doc.txt");
//
//        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(newFile.toPath(), CREATE, APPEND))) {
//            byte[] bytes = (txt.toString() + "\n" + "Joao modificou" + "\n").getBytes();
//            out.write(bytes, 0, bytes.length);
//        }
}