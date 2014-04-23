package com.tatsus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class Controller implements Initializable {

    @FXML
    private ComboBox envCombo;
    @FXML
    private TextField fromDirField;
    @FXML
    private TextField toDirField;
    @FXML
    private ImageView resultImageView1;
    @FXML
    private ImageView resultImageView2;
    @FXML
    private ImageView resultImageView3;
    @FXML
    private ImageView resultImageView4;
    @FXML
    private TextArea consoleTextArea1;
    @FXML
    private TextArea consoleTextArea2;
    @FXML
    private TextField envIDField;

    private Properties conf;
    private String toDirPath;
    private String envID;

    @FXML
    void handleBackupButtonAction(ActionEvent event) {
        if (fromDirField.getText().isEmpty()) {
            fromDirField.setStyle("-fx-background-color: orange");
            return;
        }
        if (toDirField.getText().isEmpty()) {
            toDirField.setStyle("-fx-background-color: orange");
            return;
        }
        BackupTask task = new BackupTask<>();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(task);
        es.shutdown();
    }

    @FXML
    void handleApplyButtonAction(ActionEvent event) {
        if (fromDirField.getText().isEmpty()) {
            fromDirField.setStyle("-fx-background-color: orange");
            return;
        }
        if (toDirField.getText().isEmpty()) {
            toDirField.setStyle("-fx-background-color: orange");
            return;
        }
        ApplyTask task = new ApplyTask<>();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(task);
        es.shutdown();
    }

    @FXML
    void handleEnvComboAction(ActionEvent event) {
        if (envCombo.getValue().equals(conf.getProperty("AP_ENV1"))) {
            toDirPath = conf.getProperty("AP_ENV1_DIR", "");
            envID = conf.getProperty("AP_ENV1_ID", "");
        } else if (envCombo.getValue().equals(conf.getProperty("AP_ENV2"))) {
            toDirPath = conf.getProperty("AP_ENV2_DIR", "");
            envID = conf.getProperty("AP_ENV2_ID", "");
        }
        toDirField.setText(toDirPath);
        envIDField.setText(envID);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conf = new Properties();
        try {
            conf.load(this.getClass().getResourceAsStream("/config.properties"));

        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<String> arrayList = new ArrayList<>();
        if (conf.getProperty("AP_ENV1", "").length() > 0) {
            arrayList.add(conf.getProperty("AP_ENV1"));
        }
        if (conf.getProperty("AP_ENV2", "").length() > 0) {
            arrayList.add(conf.getProperty("AP_ENV2"));
        }
        if (conf.getProperty("AP_ENV3", "").length() > 0) {
            arrayList.add(conf.getProperty("AP_ENV3"));
        }
        if (conf.getProperty("AP_ENV4", "").length() > 0) {
            arrayList.add(conf.getProperty("AP_ENV4"));
        }
        if (conf.getProperty("AP_ENV5", "").length() > 0) {
            arrayList.add(conf.getProperty("AP_ENV5"));
        }
        envCombo.getItems().addAll(arrayList);
    }

    class BackupTask<Object> extends Task {

        @Override
        protected Object call() throws Exception {
            try {
                String cmd = "cmd /c " + fromDirField.getText() + "/" + conf.getProperty("BAT1");
                Process process = Runtime.getRuntime().exec(cmd);
                InputStream in = process.getInputStream();
                InputStream ein = process.getErrorStream();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "SJIS"))) {
                    br.lines()
                            .forEach(s -> {
                                Platform.runLater(() -> {
                                    consoleTextArea1.appendText(s + System.lineSeparator());
                                });
                            });
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(ein, "SJIS"))) {
                    br.lines()
                            .forEach(s -> {
                                Platform.runLater(() -> {
                                    consoleTextArea2.appendText(s + System.lineSeparator());
                                });
                            });
                }
                int exitValue = process.waitFor();
                process.destroy();
                Platform.runLater(() -> {
                    if (exitValue == 0) {
                        resultImageView1.setVisible(true);
                        resultImageView2.setVisible(false);
                    } else {
                        resultImageView1.setVisible(false);
                        resultImageView2.setVisible(true);
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    class ApplyTask<Object> extends Task {

        @Override
        protected Object call() throws Exception {
            try {
                String cmd = "cmd /c " + fromDirField.getText() + "/" + conf.getProperty("BAT2" + " " + envID);
                Process process = Runtime.getRuntime().exec(cmd);
                InputStream in = process.getInputStream();
                InputStream ein = process.getErrorStream();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "SJIS"))) {
                    br.lines()
                            .forEach(s -> {
                                Platform.runLater(() -> {
                                    consoleTextArea1.appendText(s + System.lineSeparator());
                                });
                            });
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(ein, "SJIS"))) {
                    br.lines()
                            .forEach(s -> {
                                Platform.runLater(() -> {
                                    consoleTextArea2.appendText(s + System.lineSeparator());
                                });
                            });
                }
                int exitValue = process.waitFor();
                process.destroy();
                Platform.runLater(() -> {
                    if (exitValue == 0) {
                        resultImageView3.setVisible(true);
                        resultImageView4.setVisible(false);
                    } else {
                        resultImageView3.setVisible(false);
                        resultImageView4.setVisible(true);
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
}
