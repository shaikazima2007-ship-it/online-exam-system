import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;


public class StartPage extends Application {

    @Override
    public void start(Stage stage) {

        // ================= LOGO =================
        ImageView logo = new ImageView(
                new Image("file:/Users/shaikazima/Downloads/RTRPlogo.png")
        );

        logo.setFitWidth(160);
        logo.setPreserveRatio(true);

        Label title = new Label("ExamiX (NextGen)");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Online Assessment Platform");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #eeeeee;");

        Button startBtn = new Button("Commence Test...");
        startBtn.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff);" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 25;" +
                        "-fx-background-radius: 25;"
        );

        // ================= SPLASH CARD =================
        VBox splashCard = new VBox(10, logo, title, subtitle, startBtn);
        splashCard.setStyle(
                "-fx-alignment: center;" +
                        "-fx-padding: 30;" +
                        "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: rgba(255,255,255,0.3);" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.3, 0, 6);"
        );

        StackPane splashRoot = new StackPane(splashCard);
        splashRoot.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #141e30, #243b55);"
        );

        Scene splashScene = new Scene(splashRoot, 500, 400);

        // ================= LOGIN PAGE =================
        Label loginTitle = new Label("LOGIN");
        loginTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: #2196F3;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 20;" +
                        "-fx-background-radius: 8;"
        );

        VBox loginCard = new VBox(12, loginTitle, username, password, loginBtn);
        loginCard.setStyle(
                "-fx-alignment: center;" +
                        "-fx-padding: 30;" +
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, gray, 15, 0.3, 0, 4);" +
                        "-fx-max-width: 250;"
        );

        VBox loginRoot = new VBox(loginCard);
        loginRoot.setStyle(
                "-fx-alignment: center;" +
                        "-fx-background-color: linear-gradient(to bottom right, #ffecd2, #fcb69f);"
        );

        Scene loginScene = new Scene(loginRoot, 500, 400);

        // ================= INSTRUCTIONS PAGE =================
//        Label instTitle = new Label("EXAM INSTRUCTIONS");
//        instTitle.setStyle(
//                "-fx-font-size: 24px;" +
//                        "-fx-font-weight: bold;" +
//                        "-fx-text-fill: #ffffff;"
//        );
//
//        Label rules = new Label(
//                "✔ Do not switch tabs or minimize the window\n\n" +
//                        "✔ Total time is fixed and auto-submitted\n\n" +
//                        "✔ No malpractice or external help allowed\n\n" +
//                        "✔ Ensure stable internet connection\n\n" +
//                        "✔ Click submit before time ends"
//        );
//
//        rules.setStyle(
//                "-fx-font-size: 14px;" +
//                        "-fx-text-fill: #e0e0e0;" +
//                        "-fx-line-spacing: 6px;"
//        );
//
//        Label warning = new Label("⚠ Violations may lead to disqualification");
//        warning.setStyle(
//                "-fx-text-fill: #ffcc00;" +
//                        "-fx-font-size: 13px;" +
//                        "-fx-font-weight: bold;"
//        );
//        CheckBox agreeBox = new CheckBox("I have read and agree to the instructions");
//        agreeBox.setStyle(
//                "-fx-text-fill: #ffffff;" +
//                        "-fx-font-size: 13px;"
//        );
//
//        Button continueBtn = new Button("I Understand & Continue");
//        continueBtn.setDisable(true); // initially disabled
//
//        continueBtn.setStyle(
//                "-fx-font-size: 15px;" +
//                        "-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff);" +
//                        "-fx-text-fill: white;" +
//                        "-fx-padding: 10 25;" +
//                        "-fx-background-radius: 25;" +
//                        "-fx-opacity: 0.6;"
//        );
//        agreeBox.setOnAction(e -> {
//            boolean checked = agreeBox.isSelected();
//            continueBtn.setDisable(!checked);
//
//            continueBtn.setStyle(
//                    checked ?
//                            "-fx-font-size: 15px;" +
//                                    "-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff);" +
//                                    "-fx-text-fill: white;" +
//                                    "-fx-padding: 10 25;" +
//                                    "-fx-background-radius: 25;" :
//
//                            "-fx-font-size: 15px;" +
//                                    "-fx-background-color: #999;" +
//                                    "-fx-text-fill: white;" +
//                                    "-fx-padding: 10 25;" +
//                                    "-fx-background-radius: 25;" +
//                                    "-fx-opacity: 0.6;"
//            );
//        });

//        VBox card = new VBox(15,
//                instTitle,
//                rules,
//                warning,
//                agreeBox,
//                continueBtn
//        );
//        card.setStyle(
//                "-fx-padding: 30;" +
//                        "-fx-background-color: rgba(255,255,255,0.08);" +
//                        "-fx-background-radius: 20;" +
//                        "-fx-border-color: rgba(255,255,255,0.2);" +
//                        "-fx-border-radius: 20;" +
//                        "-fx-alignment: center;" +
//                        "-fx-max-width: 400;"
//        );
//
//        VBox root = new VBox(card);
//        root.setStyle(
//                "-fx-alignment: center;" +
//                        "-fx-background-color: linear-gradient(to bottom right, #141e30, #243b55);"
//        );
//
//        Scene instScene = new Scene(root, 500, 400);

        // ================= FLOW =================
        stage.setScene(splashScene);
        stage.setTitle("Welcome");
        stage.show();

        // Splash → Instructions
        startBtn.setOnAction(e -> {
            try {

                Stage loginStage = new Stage();

                new LoginPage().start(loginStage);

                stage.close();

            } catch (Exception ex) {

                ex.printStackTrace();

            }
        });

        // Instructions → LoginPage.java
//        continueBtn.setOnAction(e -> {
//            try {
//                Stage loginStage = new Stage();
//                new LoginPage().start(loginStage);
//                stage.close();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}