package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {

    private static ArrayList<File> inputtedFiles = new ArrayList<>();
    private static File output;
    private static int times_to_split=0;
    @FXML
    Label message;


    private static String getFileExtension(File file) {
        String extension;
        String name = file.getName();
        extension = name.substring(name.lastIndexOf("."));
        return extension;
    }

    @FXML
    public void InputChooser() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(message.getScene().getWindow());
        if (selectedFile != null) {
            inputtedFiles.add(selectedFile);
        }
    }

    @FXML
    public void OutputChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(message.getScene().getWindow());
        if(file!=null){
            output=file;
        }
    }

    @FXML
    public void Divide() throws IOException {
        if (inputtedFiles.size()==1&&output!=null) {
            TextInputDialog dialog = new TextInputDialog();

            dialog.setHeaderText("Enter How Many Parts Do You Want");

            Optional<String> result = dialog.showAndWait();
            try {
                result.ifPresent(name -> {
                    times_to_split = Integer.parseInt(name);
                    message.setVisible(false);
                });
            } catch (NumberFormatException E) {
                message.setVisible(true);
                message.setText("Please Enter a Number");
            }
            part();
            message.setVisible(true);
            message.setText("Done!");
        }else {
            message.setVisible(true);
            message.setText("Choose a File first");
        }

        inputtedFiles.clear();
        output = null;
    }

    @FXML
    public void combine() throws IOException {
        if (inputtedFiles.size() > 1 && output != null) {
            message.setVisible(false);
            File file = new File(output.getAbsolutePath()+File.separator+"Combined"+ getFileExtension(inputtedFiles.get(0)));
            FileOutputStream outputStream = new FileOutputStream(file);
            for (File inputFile : inputtedFiles) {
                FileInputStream inputStream = new FileInputStream(inputFile);
                int bytes;
                while ((bytes = inputStream.read()) != -1) {
                    outputStream.write(bytes);
                }
                inputStream.close();
            }
            outputStream.close();
            message.setVisible(true);
            message.setText("Done!");
        }else {
            message.setVisible(true);
            message.setText("Choose a File first");
        }
        inputtedFiles.clear();
        output = null;
    }

    private void part() throws IOException {
        if (inputtedFiles.size() == 1 && output != null) {
            int partCounter = 1;
            int sizeOfFiles = (int) (inputtedFiles.get(0).length() / (times_to_split));
            byte[] buffer = new byte[sizeOfFiles];
            String fileName = inputtedFiles.get(0).getName();


            try (FileInputStream fis = new FileInputStream(inputtedFiles.get(0));
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                int bytesAmount;
                while ((bytesAmount = bis.read(buffer)) > 0) {
                    String filePartName = output.getAbsolutePath()+File.separator+ String.format("%s.%03d", fileName, partCounter++) + getFileExtension(inputtedFiles.get(0));
                    File newFile = new File(filePartName);
                    try (FileOutputStream out = new FileOutputStream(newFile)) {
                        out.write(buffer, 0, bytesAmount);
                    }
                }
            }
        }
    }
}