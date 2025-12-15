package main;

import java.sql.Connection;

import database.DBConnection;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // 1. Cek koneksi database
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            // 2. Jika gagal, tampilkan alert dan hentikan aplikasi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Koneksi Database Gagal");
            alert.setContentText(
                "Tidak dapat terhubung ke database.\n" +
                "Pastikan MySQL aktif dan database tersedia."
            );
            alert.showAndWait();

            // Tutup aplikasi
            System.exit(0);
            return;
        }

        // 3. Jika berhasil, lanjut ke Login
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
