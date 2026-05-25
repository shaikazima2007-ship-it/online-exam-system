import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Random;
import java.sql.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
public class LoginPage extends Application {
    @Override
    public void start(Stage stage) {
        // ---------------- TITLE ----------------
        Label title = new Label("Online Exam Login");
        title.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
        // ROLL NUMBER
        TextField rollno = new TextField();
        rollno.setPromptText("Enter Roll Number");
        // ---------------- USERNAME ----------------
        TextField username = new TextField();
        username.setPromptText("Enter Username");
        username.requestFocus();
        // ---------------- PASSWORD ----------------
        PasswordField password = new PasswordField();
        TextField passwordVisible = new TextField();
        Button toggleBtn = new Button("Show");
        password.setPromptText("Enter Password");
        passwordVisible.setManaged(false);
        passwordVisible.setVisible(false);
        passwordVisible.setPromptText("Enter Password");
        // sync text
        password.textProperty().bindBidirectional(passwordVisible.textProperty());
        toggleBtn.setOnAction(e -> {
            boolean showing = password.isVisible();
            password.setVisible(!showing);
            password.setManaged(!showing);
            passwordVisible.setVisible(showing);
            passwordVisible.setManaged(showing);
            toggleBtn.setText(showing ? "Hide" : "Show");
        });
        HBox passwordBox = new HBox(10, password, passwordVisible, toggleBtn);
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        // ---------------- CAPTCHA ----------------
        Canvas captchaCanvas = new Canvas(150, 50);
        TextField captchaInput = new TextField();
        captchaInput.setPromptText("Enter Captcha");
        Button refreshBtn = new Button("Refresh Captcha");
        String[] captchaHolder = new String[1];
        generateCaptcha(captchaCanvas, captchaHolder);
        refreshBtn.setOnAction(e -> generateCaptcha(captchaCanvas, captchaHolder));
        // ---------------- LOGIN ----------------
        Button loginBtn = new Button("Login");
        Label result = new Label();
        int[] attempts = {0};
        final int MAX_ATTEMPTS = 3;
        // ---------------- LAYOUT ----------------
        VBox layout = new VBox(15,
                title,
                new Label("Roll Number"),
                rollno,
                new Label("Username"),
                username,
                new Label("Password"),
                passwordBox,
                new Label("Captcha"),
                captchaCanvas,
                captchaInput,
                refreshBtn,
                loginBtn,
                result
        );
        layout.setPadding(new Insets(20));
        layout.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #dfe9f3, #ffffff);" +
                        "-fx-padding: 25;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.2, 0, 4);"
        );
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(12);
        rollno.setStyle("-fx-padding:10; -fx-font-size:14;");
        username.setStyle("-fx-padding:10; -fx-font-size:14;");
        password.setStyle("-fx-padding:10; -fx-font-size:14;");
        captchaInput.setStyle("-fx-padding:10; -fx-font-size:14;");
        loginBtn.setStyle(
                "-fx-background-color:#4CAF50;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-background-radius:10;"
        );
        refreshBtn.setStyle(
                "-fx-background-color:#2196F3;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:10;"
        );
        title.setStyle(
                "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#2c3e50;"
        );
        loginBtn.setOnAction(e ->
                handleLogin(rollno,username, password, captchaInput,
                        captchaHolder, result, stage, attempts, MAX_ATTEMPTS,layout));
        Scene scene = new Scene(layout, 400, 600);
        // ---------------- ENTER KEY SUPPORT ----------------
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                    handleLogin(rollno,username, password, captchaInput,
                            captchaHolder, result, stage, attempts, MAX_ATTEMPTS,layout);
                    break;
            }
        });
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.show();
    }
    // ---------------- LOGIN LOGIC ----------------
    private void handleLogin(
            TextField rollno,
            TextField username,
            PasswordField password,
            TextField captchaInput,
            String[] captchaHolder,
            Label result,
            Stage stage,
            int[] attempts,
            int MAX_ATTEMPTS,
            VBox layout
    ) {
        String roll = rollno.getText();
        String user = username.getText();
        String pass = password.getText();
        String captcha = captchaInput.getText();
        // ---------------- BLOCK ----------------
        if (attempts[0] >= MAX_ATTEMPTS) {
            result.setText("Login Blocked!");
            return;
        }
        // ---------------- CAPTCHA CHECK ----------------
        if (!captcha.equals(captchaHolder[0])) {
            attempts[0]++;
            result.setText("Wrong Captcha! Attempts Left: " +
                    (MAX_ATTEMPTS - attempts[0]));
            return;
        }
        String role = null;
        String studentName = null;
        String rollNo = null;
        String photoPath = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/Online_db",
                    "root",
                    "Azima@2007"
            );
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT role, username, rollno, photo_path " +
                            "FROM users WHERE rollno=? AND username=? AND password=?"
            );
            ps.setString(1, roll);
            ps.setString(2, user);
            ps.setString(3, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");
                studentName = rs.getString("username");
                rollNo = rs.getString("rollno");
                photoPath = rs.getString("photo_path");
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setText(ex.getMessage());
            return;
        }
        // ---------------- LOGIN SUCCESS ----------------
        if (role != null) {
            result.setText("Login Successful: " + role);
            try {
                // STUDENT
                if (role.equals("STUDENT")) {
                    if (!Database.examEnabled()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Exam Not Available");
                        alert.setHeaderText("Exam Currently Disabled");
                        alert.setContentText("The examination has not started yet.\n\n" +
                                        "Please wait until admin enables it."
                        );
                        alert.showAndWait();
                        return;
                    }
                    final String finalStudent = studentName;
                    final String finalRoll = rollNo;
                    final String finalPhoto = photoPath;
                    Label instTitle = new Label("EXAM INSTRUCTIONS");
                    instTitle.setStyle(
                            "-fx-font-size:24px;" +
                                    "-fx-font-weight:bold;" +
                                    "-fx-text-fill:white;"
                    );
                    Label rules = new Label(
                            "✔ No tab switching\n\n" +
                                    "✔ Screenshots prohibited\n\n" +
                                    "✔ Webcam monitoring active\n\n" +
                                    "✔ Auto-submit on violations\n\n" +
                                    "✔ Timer cannot be paused"
                    );
                    rules.setStyle(
                            "-fx-font-size:14px;" +
                                    "-fx-text-fill:#e0e0e0;"
                    );
                    Label warning =
                            new Label(
                                    "⚠ Violations may lead to disqualification"
                            );

                    warning.setStyle(
                            "-fx-text-fill:#ffcc00;" +
                                    "-fx-font-weight:bold;"
                    );

                    CheckBox agree =
                            new CheckBox(
                                    "I agree to instructions"
                            );

                    agree.setStyle(
                            "-fx-text-fill:white;"
                    );

                    Button continueBtn =
                            new Button(
                                    "I Understand & Continue"
                            );

                    continueBtn.setDisable(true);

                    continueBtn.setStyle(
                            "-fx-background-color:linear-gradient(to right,#00c6ff,#0072ff);" +
                                    "-fx-text-fill:white;" +
                                    "-fx-background-radius:20;"
                    );

                    agree.setOnAction(e ->
                            continueBtn.setDisable(
                                    !agree.isSelected()
                            )
                    );

                    VBox card =
                            new VBox(
                                    15,
                                    instTitle,
                                    rules,
                                    warning,
                                    agree,
                                    continueBtn
                            );

                    card.setAlignment(Pos.CENTER);

                    card.setStyle(
                            "-fx-background-color:rgba(255,255,255,0.08);" +
                                    "-fx-padding:30;" +
                                    "-fx-background-radius:20;"
                    );

                    StackPane root =
                            new StackPane(card);

                    root.setStyle(
                            "-fx-background-color:" +
                                    "linear-gradient(to bottom right,#141e30,#243b55);"
                    );

                    Stage instStage =
                            new Stage();

                    instStage.setScene(
                            new Scene(root,500,400)
                    );

                    instStage.setTitle(
                            "Exam Instructions"
                    );

                    continueBtn.setOnAction(ev -> {

                        try {

                            instStage.close();

                            ExamPage exam =
                                    new ExamPage(
                                            finalStudent,
                                            finalRoll,
                                            finalPhoto
                                    );

                            Stage examStage = new Stage();

                            exam.start(examStage);

                            stage.close();

                        }

                        catch(Exception ex){

                            ex.printStackTrace();

                            Alert err =
                                    new Alert(Alert.AlertType.ERROR);

                            err.setContentText(
                                    ex.getMessage()
                            );

                            err.showAndWait();

                        }

                    });

                    instStage.show();
                }
                // ADMIN
                else if (role.equals("ADMIN")) {

                    stage.hide();

                    new AdminDashboard().start(new Stage());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                result.setText("Error opening next page!");
            }

        } else {
            attempts[0]++;
            result.setText("Invalid Username/Password! Attempts Left: " +
                    (MAX_ATTEMPTS - attempts[0]));
            shakeNode(layout);
        }
    }

    // ---------------- CAPTCHA ----------------
    private void generateCaptcha(Canvas canvas, String[] holder) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        gc.setFont(new Font(22));

        for (int i = 0; i < 6; i++) {
            char c = chars.charAt(random.nextInt(chars.length()));
            sb.append(c);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(c), 20 * i + 10, 30);
        }

        holder[0] = sb.toString();
    }
    private void shakeNode(javafx.scene.Node node) {

        javafx.animation.TranslateTransition tt =
                new javafx.animation.TranslateTransition(
                        javafx.util.Duration.millis(50),
                        node
                );

        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    public static void main(String[] args) {
        launch();
    }
}