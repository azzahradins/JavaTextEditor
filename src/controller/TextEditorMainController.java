package controller;

import com.sun.tools.javac.resources.legacy;
import form.TextEditor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import jdk.internal.jline.internal.Log;

public class TextEditorMainController {

    private final TextEditor view;
    private String[] pathnames = null;
    private String fileName = "";
    private String directory;
    private List<Integer> list = new ArrayList<>();

    /*
    Logika Aplikasi Text Editor :
        1. Buka file
        2. Sebelum simpan file cek dulu apakah ada nama file yang sama
        3. Jika tidak ada nama file yang sama tidak perlu mengeluarkan filechooser
        4. Selesai
    */

    public TextEditorMainController(TextEditor textEditor) {
        this.view = textEditor;
    }
    
    public void fileBaru(){
        pathnames = null;
        fileName = "";
    }
    
    public void bukaFile() {
        // ubah view sehingga 
        JFileChooser loadFile = view.getFileChooser();
        StyledDocument doc = view.getTextPane().getStyledDocument();
        if (JFileChooser.APPROVE_OPTION == loadFile.showOpenDialog(view)) {
            File file = new File(loadFile.getSelectedFile().getAbsolutePath());
            InputStream inputStream = null;
            try {
                // buat tab visible
                view.getTextTab().setVisible(true);
                view.getTextPane().setText("");
                // simpan file path untuk identifikasi saat save file
                pathnames = loadFile.getCurrentDirectory().list();
                fileName = file.getName();
                directory = loadFile.getCurrentDirectory().getPath();
                System.out.println("File Open : " + directory);
                inputStream = new FileInputStream(loadFile.getSelectedFile());
                String nameFile = file.getName();
                int read = inputStream.read();
                doc.insertString(0, "", null);
                while (read != -1) {
                    //set tab title and document inside
                    list.add(read);// tambahkan 1 baris
                    view.getTextTab().setTitleAt(0, nameFile);
                    doc.insertString(doc.getLength(), "" + Character.toString((char) read), null);
                    read = inputStream.read();
                }
                inputStream.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | BadLocationException ex) {
                Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public void bukaFile(File inputFile) {
        try {
            // simpan file path untuk identifikasi saat save file
            System.out.println("activating file on : " + directory);
            InputStream inputStream = new FileInputStream(inputFile);
            fileName = inputFile.getName();
            int read = inputStream.read();
            while (read != -1) {
                //set tab title and document inside
                list.add(read);// tambahkan 1 baris
                view.getTextTab().setTitleAt(0, fileName);
                read = inputStream.read();
            }
            inputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void simpanFile() {
        if (pathnames != null) {
            if (cekFile(fileName)) {
                // kalau ada nama file yang sama
                prosesSimpan();
            } else {
                // kalau tidak ada nama file yang sama
                prosesSimpanBaru();
            }
        } else {
            prosesSimpanBaru();
        }
    }

    public boolean cekFile(String fileName) {
        // update folder untuk setiap nama file  yang ada
        File currentFile = new File(directory);
        pathnames = currentFile.list();
        for (String pathname : pathnames) {
            // kalau ditemukan nama file yang sama
            if (pathname.equals(fileName)) {
                System.out.println(fileName + " founded!");
                return true;
            }
        }
        System.out.println(fileName + " not found!");
        return false;
    }

    public void prosesSimpanBaru() {
        JFileChooser loadFile = view.getFileChooser();
        if (JFileChooser.APPROVE_OPTION == loadFile.showSaveDialog(view)) {
            File file = new File(loadFile.getSelectedFile().getAbsolutePath());
            BufferedWriter writer = null;
            try {
                String contents = view.getTextPane().getText();
                if (contents != null && !contents.isEmpty()) {
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.write(contents);
                }
                writer.flush();
                writer.close();
                
                //setelah file berhasil disimpan baru tutup dengan simpan ke directory
                System.out.println("File baru berhasil disimpan!");
                pathnames = loadFile.getCurrentDirectory().list();
                directory = loadFile.getCurrentDirectory().getAbsolutePath();
                fileName = file.getName();
                view.getTextTab().setTitleAt(0, fileName);
                System.out.println(pathnames.length);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void prosesSimpan() {
        System.out.println("Apakah PROSES SIMPAN dijalankan?");
        File file = new File(directory + "/" + fileName);
        bukaFile(file);
        OutputStream os = null;
        try {
            String contents = view.getTextPane().getText();
            if (!list.isEmpty()) {
                os = new FileOutputStream(file);
                fileName = file.getName();
                view.getTextTab().setTitleAt(0, fileName);
                byte[] dt = contents.getBytes();
                os.write(dt);
                os.flush();
                os.close();
                list.clear();
                System.out.println("File berhasil disimpan!");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextEditorMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
