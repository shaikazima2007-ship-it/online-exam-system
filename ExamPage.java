import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Label;
import java.util.Random;
import java.sql.*;
import java.util.ArrayList;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import java.util.List;
import java.util.Collections;

public class ExamPage extends Application {
    public static String studentNameStatic;
    public static String rollNoStatic;
    public static String photoPathStatic;
    int warnings = 0;
    final int MAX_WARNINGS = 3;
    boolean isWarningDialogOpen = false;
    Timeline timer;
    // ---------------- Student ----------------
    private String studentName;
    private String rollNo;
    private String photoPath;
    public ExamPage(String studentName,
                    String rollNo,
                    String photoPath){
        ExamPage.studentNameStatic = studentName;
        ExamPage.rollNoStatic = rollNo;
        ExamPage.photoPathStatic = photoPath;
    }
    public ExamPage() {
        this.studentNameStatic = "UNKNOWN"; // prevents NULL in DB
    }
    // ---------------- State ----------------
    Button reviewBtn;
    int currentQuestion = 0;
    int score = 0;
    int timeLeft;
    int totalQuestions;
    boolean isSubmitted = false;
    ArrayList<String> questions =
            new ArrayList<>();
    ArrayList<String[]> options =
            new ArrayList<>();
    ArrayList<String> answersKey =
            new ArrayList<>();
    ArrayList<String> questionTypes =
            new ArrayList<>();
    // ---------------- Questions ----------------
    String[] userAnswers = new String[100];
    // ---------------- UI ----------------
    Label questionLabel = new Label();
    {
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(500);
        questionLabel.setStyle(
                "-fx-font-size:18px;" +
                        "-fx-font-weight:bold;"
        );
    }
    Label timerLabel = new Label();
    VBox questionBox;
    RadioButton o1 = new RadioButton();
    RadioButton o2 = new RadioButton();
    RadioButton o3 = new RadioButton();
    RadioButton o4 = new RadioButton();
    {
        o1.setWrapText(true);
        o2.setWrapText(true);
        o3.setWrapText(true);
        o4.setWrapText(true);
        o1.setMaxWidth(450);
        o2.setMaxWidth(450);
        o3.setMaxWidth(450);
        o4.setMaxWidth(450);
    }
    TextField answerField =
            new TextField();
    {
        answerField.setPrefWidth(300);
        answerField.setPrefHeight(40);
    }
    ToggleGroup group = new ToggleGroup();
    @Override
    public void start(Stage stage) {
        loadExamSettings();
        loadStudentDetails();
        timerLabel.setText("Time: " + timeLeft);
        answerField.setPromptText(
                "Type answer here"
        );
        answerField.setVisible(false);
        Label rollLabel = new Label("Roll No : " + rollNoStatic);
        Label nameLabel = new Label("Student: " + studentNameStatic);
        nameLabel.setStyle(
                "-fx-font-size:14px;" +
                        "-fx-text-fill:#2c3e50;" +
                        "-fx-font-weight:bold;"
        );
        Label title = new Label("Online Exam System");
        ImageView studentPhoto = new ImageView();
        System.out.println("PHOTO PATH = " + photoPathStatic);
        try {
            if (photoPathStatic != null && !photoPathStatic.trim().isEmpty()) {
                File file = new File(photoPathStatic);
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString());
                    studentPhoto.setImage(img);
                } else {
                    System.out.println("❌ File NOT found: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("❌ photoPathStatic is null/empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        studentPhoto.setFitWidth(90);
        studentPhoto.setFitHeight(90);
        Label webcamLabel =
                new Label("WEBCAM ACTIVE");
        Label faceLabel =
                new Label("Face Detected");
        webcamLabel.setStyle(
                "-fx-font-size:16px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:green;"
        );
        faceLabel.setStyle(
                "-fx-font-size:14px;" +
                        "-fx-text-fill:blue;"
        );
        title.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
        timerLabel.setStyle("-fx-text-fill:red;");
        // Radio Group
        o1.setToggleGroup(group);
        o2.setToggleGroup(group);
        o3.setToggleGroup(group);
        o4.setToggleGroup(group);
        // Buttons
        Button nextBtn = new Button("Next");
        Button prevBtn = new Button("Previous");
        Button submitBtn = new Button("Submit");
         reviewBtn = new Button("Review Answers");
        reviewBtn.setStyle(
                "-fx-background-color:#ff9800;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:8;"
        );
        reviewBtn.setDisable(true);   // IMPORTANT
        reviewBtn.setVisible(false);  // IMPORTANT
        reviewBtn.setOnAction(e -> showReviewScreen());
        // Load First Questiona
        loadQuestionsFromDB();
        System.out.println(
                "Questions Loaded = "
                        + questions.size()
        );
        loadQuestion();
        // ---------------- BUTTON ACTIONS ----------------
        nextBtn.setOnAction(e -> {
            saveAnswer();
            if (currentQuestion < Math.min(questions.size(), totalQuestions) - 1) {
                currentQuestion++;
                loadQuestion();
            }
        });
        prevBtn.setOnAction(e -> {
            saveAnswer();
            if (currentQuestion > 0) {
                currentQuestion--;
                loadQuestion();
            }
        });
        submitBtn.setOnAction(e -> {
            saveAnswer();
            calculateScore(stage);
        });
        // ---------------- Layout ----------------
        questionBox = new VBox(
                10,
                questionLabel,
                o1,
                o2,
                o3,
                o4,
                answerField
        );
        questionBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 15;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 10;"
        );
        HBox buttonBox = new HBox(10,
                prevBtn,
                nextBtn,
                submitBtn,
                reviewBtn
        );
        buttonBox.setAlignment(Pos.CENTER);
        prevBtn.setStyle("-fx-background-color:#95a5a6; -fx-text-fill:white;");
        nextBtn.setStyle("-fx-background-color:#3498db; -fx-text-fill:white;");
        submitBtn.setStyle("-fx-background-color:#2ecc71; -fx-text-fill:white;");
        VBox textBox = new VBox(
                5,
                title,
                nameLabel,
                rollLabel,
                webcamLabel,
                faceLabel
        );
        HBox topInfo = new HBox(20);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topInfo.getChildren().addAll(textBox, spacer, studentPhoto);

        topInfo.setAlignment(Pos.CENTER_LEFT);

        topInfo.setAlignment(Pos.CENTER_LEFT);
        topInfo.setStyle(
                "-fx-background-color: #f4f6f8;" +
                        "-fx-padding: 12;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 10;"
        );

        VBox layout =
                new VBox(
                        20,
                        topInfo,
                        timerLabel,
                        questionBox,
                        buttonBox
                );
        layout.setStyle("-fx-background-color: #f0f2f5;");
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setSpacing(15);
        layout.setPadding(new Insets(20));
//        layout.setPadding(new Insets(25));
//        layout.setAlignment(Pos.TOP_CENTER);
//        layout.setStyle(
//                "-fx-font-size:15px;"
//        );
//
//        questionLabel.setStyle(
//                "-fx-font-size:18px; -fx-font-weight:bold;"
//        );
//
//        o1.setStyle("-fx-font-size:14px;");
//        o2.setStyle("-fx-font-size:14px;");
//        o3.setStyle("-fx-font-size:14px;");
//        o4.setStyle("-fx-font-size:14px;");
//
//        layout.setPrefWidth(800);

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 500);
        scene.setOnKeyPressed(e -> {

            if (isSubmitted) return;

            // WINDOWS PRINT SCREEN
            if (e.getCode().toString().equals("PRINTSCREEN")) {

                if (isSubmitted) return;
                if (isWarningDialogOpen) return;

                isWarningDialogOpen = true;
                warnings++;

                Database.saveViolation(studentNameStatic, warnings);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Screenshot attempt detected.\nWarning " + warnings + " of 3"
                );

                alert.showAndWait();   // ⭐ IMPORTANT FIX

                isWarningDialogOpen = false;

                if (warnings >= MAX_WARNINGS) {
                    calculateScore(stage);
                }
            }

            // MAC SCREENSHOT (CMD + SHIFT)
            if (e.isMetaDown() && e.isShiftDown() && e.getCode() != null) {
                if (isWarningDialogOpen) return;
                isWarningDialogOpen = true;

                warnings++;
                Database.saveViolation(studentNameStatic, warnings);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Exam Warning");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Screenshot attempt detected.\nWarning " + warnings + " of 3"
                );

                alert.setOnHidden(ev -> isWarningDialogOpen = false);
                alert.showAndWait();

                if (warnings >= MAX_WARNINGS) {
                    calculateScore(stage);
                }
            }
        });


        stage.setScene(scene);

        stage.setTitle("Exam Page");

//        stage.setMaximized(true);
//
//        stage.setFullScreen(true);
//
//        stage.setFullScreenExitHint("");

        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            warnings++;
            Database.saveViolation(studentNameStatic, warnings);
        });

        stage.focusedProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal) {
                triggerWarning(stage);
            }
        });
        // ---------------- Timer ----------------

        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {

                    if (timeLeft > 0) {
                        timeLeft--;
                        timerLabel.setText("Time: " + timeLeft);
                    } else {
                        calculateScore(stage);
                    }

                })
        );

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        Random random = new Random();

        Timeline webcamMonitor =
                new Timeline(

                        new KeyFrame(
                                Duration.seconds(5),

                                e -> {

                                    boolean faceDetected =
                                            random.nextInt(10) > 1;

                                    if (faceDetected) {

                                        faceLabel.setText(
                                                "Face Detected"
                                        );

                                        faceLabel.setStyle(
                                                "-fx-text-fill:green;"
                                        );
                                    } else {

                                        faceLabel.setText(
                                                "No Face Detected"
                                        );

                                        faceLabel.setStyle(
                                                "-fx-text-fill:red;"
                                        );

                                        warnings++;

                                        Database.saveViolation(
                                                studentNameStatic,
                                                warnings
                                        );

                                        Alert alert =
                                                new Alert(
                                                        Alert.AlertType.WARNING
                                                );

                                        alert.setTitle(
                                                "Proctoring Alert"
                                        );

                                        alert.setHeaderText(null);

                                        alert.setContentText(
                                                "No face detected!\n" +
                                                        "Warning "
                                                        + warnings
                                                        + " of 3"
                                        );

                                        alert.showAndWait();

                                        if (warnings >= 3) {

                                            calculateScore(stage);

                                            Alert autoSubmit =
                                                    new Alert(
                                                            Alert.AlertType.ERROR
                                                    );

                                            autoSubmit.setTitle(
                                                    "Exam Auto Submitted"
                                            );

                                            autoSubmit.setHeaderText(null);

                                            autoSubmit.setContentText(
                                                    "Exam terminated due to webcam violations."
                                            );

                                            autoSubmit.showAndWait();

                                            stage.close();
                                        }
                                    }
                                }
                        )
                );

        webcamMonitor.setCycleCount(
                Timeline.INDEFINITE
        );

        webcamMonitor.play();
    }



        private void showResultWindow(double percent, String result) {

            Stage resultStage = new Stage();

            VBox box = new VBox(15,
                    new Label("Student: " + studentNameStatic),
                    new Label("Score: " + score + "/" + totalQuestions),
                    new Label("Percentage: " + percent + "%"),
                    new Label("Result: " + result)
            );

            box.setPadding(new Insets(20));

            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(e -> resultStage.close());

            box.getChildren().add(closeBtn);

            resultStage.setScene(new Scene(box, 300, 200));
            resultStage.setTitle("Result");
            resultStage.show();
        }
    private void triggerWarning(Stage stage) {

        if (isSubmitted) return;

        warnings++;

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Suspicious activity/Tab switch detected!");
        alert.setContentText("Warning " + warnings + " of 3");

        alert.showAndWait();
        //SAVE TO MYSQL
        Database.saveViolation(studentNameStatic, warnings);


        if (warnings >= MAX_WARNINGS) {
            calculateScore(stage);
        }
    }
    private void showViolationResultWindow() {

        Stage warnStage = new Stage();

        VBox box = new VBox(15);

        Label msg = new Label("⚠ Auto Submitted Due to Violations");
        msg.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        Label detail = new Label("You exceeded 3 warnings.");

        Button closeBtn = new Button("Close");

        closeBtn.setOnAction(e -> warnStage.close());

        box.getChildren().addAll(msg, detail, closeBtn);
        box.setPadding(new Insets(20));

        Scene scene = new Scene(box, 300, 150);

        warnStage.setTitle("Exam Terminated");
        warnStage.setScene(scene);
        warnStage.show();
    }
    private void loadStudentDetails(){

        try(

                Connection conn =
                        DriverManager.getConnection(

                                "jdbc:mysql://127.0.0.1:3306/Online_db",
                                "root",
                                "Azima@2007"

                        );

                PreparedStatement ps =
                        conn.prepareStatement(

                                "SELECT rollno, photo_path FROM users WHERE rollno=?"
                        )

        ){

            ps.setString(1, rollNo);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                rollNo =
                        rs.getString("rollno");

                photoPath =
                        rs.getString("photo_path");

            }

        }

        catch(Exception e){

            e.printStackTrace();

        }

    }

    private void loadQuestionsFromDB(){

        questions.clear();
        options.clear();
        answersKey.clear();
        questionTypes.clear();

        try(

                Connection conn=
                        DriverManager.getConnection(
                                "jdbc:mysql://127.0.0.1:3306/Online_db",
                                "root",
                                "Azima@2007"
                        );

                Statement st=
                        conn.createStatement();

                ResultSet rs=
                        st.executeQuery(
                                "SELECT * FROM questionss ORDER BY RAND()"
                        )

        ){

            while(rs.next()){

                questions.add(
                        rs.getString("question")
                );

                options.add(
                        new String[]{
                                rs.getString("option1"),
                                rs.getString("option2"),
                                rs.getString("option3"),
                                rs.getString("option4")
                        }
                );

                answersKey.add(
                        rs.getString("answer")
                );

                questionTypes.add(
                        rs.getString("question_type")
                );
            }

        }

        catch(Exception e){

            e.printStackTrace();

        }

    }
    // ---------------- Load Question ----------------

    private void loadQuestion() {


        if (currentQuestion >= Math.min(questions.size(), totalQuestions))
            return;

        questionLabel.setText(
                (currentQuestion + 1) + ". " + questions.get(currentQuestion)
        );

        String type = questionTypes.get(currentQuestion);

        // 🔥 RESET EVERYTHING PROPERLY
        o1.setVisible(false);
        o2.setVisible(false);
        o3.setVisible(false);
        o4.setVisible(false);

        o1.setManaged(false);
        o2.setManaged(false);
        o3.setManaged(false);
        o4.setManaged(false);

        answerField.setVisible(false);
        answerField.setManaged(false);

        group.selectToggle(null);

        // ---------------- MCQ ----------------
        if ("MCQ".equals(type)) {

            String[] ops = options.get(currentQuestion);

            o1.setText(ops[0]);
            o2.setText(ops[1]);
            o3.setText(ops[2]);
            o4.setText(ops[3]);

            o1.setVisible(true);
            o2.setVisible(true);
            o3.setVisible(true);
            o4.setVisible(true);

            o1.setManaged(true);
            o2.setManaged(true);
            o3.setManaged(true);
            o4.setManaged(true);
        }

        // ---------------- TRUE/FALSE ----------------
        else if (type != null &&
                type.trim().equalsIgnoreCase("TRUE_FALSE")) {

            o1.setText("True");
            o2.setText("False");

            o1.setVisible(true);
            o2.setVisible(true);

            o1.setManaged(true);
            o2.setManaged(true);

            o3.setVisible(false);
            o4.setVisible(false);

            o3.setManaged(false);
            o4.setManaged(false);
        }

        // ---------------- TEXT ----------------
        else if ("TEXT".equals(type)) {

            answerField.setVisible(true);
            answerField.setManaged(true);

            if (userAnswers[currentQuestion] != null) {
                answerField.setText(userAnswers[currentQuestion]);
            } else {
                answerField.clear();
            }
        }

    }
    // ---------------- Save Answer ----------------

    private void saveAnswer(){

        String type=
                questionTypes.get(currentQuestion);

        if("TEXT".equals(type)){

            userAnswers[currentQuestion]
                    =
                    answerField.getText();

            return;
        }

        RadioButton selected=
                (RadioButton)
                        group.getSelectedToggle();

        if(selected!=null){

            userAnswers[currentQuestion]
                    =
                    selected.getText();
        }
    }
    // ---------------- Score ----------------

    private void calculateScore(Stage stage) {
        if (isSubmitted && score > 0) return;
        isSubmitted = true;
        // STOP TIMER
        if (timer != null) {
            timer.stop();
        }
        // ---------------- CALCULATE SCORE ----------------
        score = 0;
        for (int i = 0; i < Math.min(questions.size(), totalQuestions); i++) {

            String userAns = userAnswers[i];
            String correctAns = answersKey.get(i);

            if (userAns == null || correctAns == null) continue;

            String type = questionTypes.get(i);

            // MCQ / TF → strict match
            if (type.equalsIgnoreCase("MCQ") ||
                    type.equalsIgnoreCase("TRUE_FALSE")) {

                if (userAns.trim().equalsIgnoreCase(correctAns.trim())) {
                    score++;
                }
            }

            // TEXT → meaning-based match
            else {

                if (isMeaningSame(userAns, correctAns)) {
                    score++;
                }
            }
        }
        double percent = Math.round((score * 100.0) / totalQuestions * 100.0) / 100.0;
        String result;
        if (percent >= 50)
            result = "PASS";
        else
            result = "FAIL";
        Label resultLabel = new Label("Result: " + result);

        resultLabel.setStyle(
                "-fx-font-size:16px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-color:" +
                        (result.equals("PASS") ? "#2ecc71" : "#e74c3c") + ";" +
                        "-fx-padding:8 15;" +
                        "-fx-background-radius:15;"
        );
        Stage resultStage = new Stage();

// TITLE
        Label title = new Label("🏆 EXAM RESULT");
        title.setStyle(
                "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:white;"
        );

// SUCCESS MESSAGE
        Label submitMsg = new Label("✓ Exam Submitted Successfully");
        submitMsg.setStyle(
                "-fx-font-size:14px;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-color:#3498db;" +
                        "-fx-padding:8 15;" +
                        "-fx-background-radius:20;"
        );

// STUDENT NAME
        Label student =
                new Label(
                        "👤 " + studentNameStatic
                );

        student.setStyle(
                "-fx-font-size:15px;" +
                        "-fx-background-color:#f5f6fa;" +
                        "-fx-padding:8 18;" +
                        "-fx-background-radius:15;"
        );

// BIG SCORE
        Label scoreLabel = new Label(score + " / " + totalQuestions);
        scoreLabel.setStyle(
                "-fx-font-size:42px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#2c3e50;" +
                        "-fx-background-color:#ecf0f1;" +
                        "-fx-padding:15 30;" +
                        "-fx-background-radius:50;"
        );

// PERCENTAGE
        Label percentLabel =
                new Label("Percentage : " + percent + "%");

        percentLabel.setStyle(
                "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;"
        );
        String performance;

        if(percent>=90)
            performance="🌟 Outstanding";
        else if(percent>=75)
            performance="🔥 Excellent";
        else if(percent>=50)
            performance="✅ Good";
        else
            performance="📘 Needs Improvement";

        Label perf =
                new Label(performance);

        perf.setStyle(
                "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#34495e;"
        );

// PASS FAIL BADGE
        Label resultBadge = new Label(result);

        resultBadge.setStyle(
                "-fx-font-size:18px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:white;" +
                        "-fx-padding:10 30;" +
                        "-fx-background-radius:40;" +
                        "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.25),10,0.2,0,3);" +
                        "-fx-background-color:" +
                        (result.equals("PASS")
                                ? "linear-gradient(to right,#11998e,#38ef7d)"
                                : "linear-gradient(to right,#cb2d3e,#ef473a)")
        );
// BUTTONS
        Button reviewBtnResult =
                new Button("Review Answers");

        reviewBtnResult.setStyle(
                "-fx-background-color:#3498db;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:15;" +
                        "-fx-padding:8 20;"
        );

        reviewBtnResult.setOnAction(
                e -> showReviewScreen()
        );

        Button exitBtn = new Button("Exit");

        exitBtn.setStyle(
                "-fx-background-color:#e74c3c;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:15;" +
                        "-fx-padding:8 20;"
        );

        exitBtn.setOnAction(
                e -> resultStage.close()
        );

// CARD
        VBox card = new VBox(
                12,
                title,
                submitMsg,
                student,
                scoreLabel,
                percentLabel,
                resultBadge,
                reviewBtnResult,
                exitBtn
        );

        card.setAlignment(Pos.CENTER);

        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:25;" +
                        "-fx-padding:30;" +
                        "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.25),20,0.3,0,5);"
        );

// ROOT
        StackPane root = new StackPane(card);

        root.setStyle(
                "-fx-background-color:" +
                        "linear-gradient(to bottom right,#141e30,#243b55);"
        );

        Scene scene =
                new Scene(root,470,520);
        Label thanks =
                new Label(
                        "Thank you for participating"
                );

        thanks.setStyle(
                "-fx-text-fill:gray;" +
                        "-fx-font-size:12px;"
        );

        resultStage.setScene(scene);

        resultStage.setTitle("Exam Result");

        resultStage.show();

        stage.close();














        // ---------------- SAVE TO DATABASE ----------------

        try {

            System.out.println("Saving result now...");

            System.out.println("STUDENT = " + studentNameStatic);
            System.out.println("SCORE = " + score);
            System.out.println("TOTAL = " + questions.size());
            System.out.println("PERCENT = " + percent);
            System.out.println("RESULT = " + result);
            System.out.println("WARNINGS = " + warnings);

            Database.saveResult(
                    studentNameStatic,
                    score,
                    questions.size(),
                    percent,
                    result,
                    warnings
            );
            System.out.println("Result saved successfully!");
            ReportGenerator.generateReport(
                    studentNameStatic,
                    score,
                    questions.size(),
                    percent,
                    result,
                    warnings
            );

        } catch (Exception e) {

            System.out.println("Error while saving result");

            e.printStackTrace();
        }

        // ---------------- RESULT WINDOW ----------------








// close exam window AFTER result
        reviewBtn.setVisible(true);
        reviewBtn.setDisable(false);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Exam Submitted");
        a.setContentText("You can now review your answers.");
        a.showAndWait();
        stage.close();




    }


    private void loadExamSettings(){

        try(

                Connection conn=

                        DriverManager.getConnection(
                                "jdbc:mysql://127.0.0.1:3306/Online_db",
                                "root",
                                "Azima@2007"
                        );

                Statement st=
                        conn.createStatement();

                ResultSet rs=

                        st.executeQuery(

                                "SELECT * FROM exam_settings WHERE id=1"

                        )

        ){

            if(rs.next()){

                timeLeft=

                        rs.getInt(
                                "exam_time"
                        );

                totalQuestions=

                        rs.getInt(
                                "total_questions"
                        );

            }

        }

        catch(Exception e){

            e.printStackTrace();

            timeLeft=60;

            totalQuestions=5;

        }

    }

    private void showReviewScreen() {

        Stage reviewStage = new Stage();

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        for (int i = 0; i < questions.size(); i++) {

            String q = questions.get(i);
            String correct = answersKey.get(i);
            String userAns = userAnswers[i];

            Label qLabel = new Label((i + 1) + ". " + q);
            Label userLabel = new Label("Your Answer: " + (userAns == null ? "Not Answered" : userAns));
            Label correctLabel = new Label("Correct Answer: " + correct);

            if (userAns != null && userAns.equals(correct)) {
                userLabel.setStyle("-fx-text-fill:green;");
            } else {
                userLabel.setStyle("-fx-text-fill:red;");
            }

            correctLabel.setStyle("-fx-text-fill:green; -fx-font-weight:bold;");

            VBox card = new VBox(3, qLabel, userLabel, correctLabel);
            card.setStyle("-fx-padding:10; -fx-border-color:gray; -fx-border-radius:5;");

            box.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 500, 500);

        reviewStage.setTitle("Review Answers");
        reviewStage.setScene(scene);
        reviewStage.show();
    }
    private boolean isMeaningSame(String user, String correct) {

        if (user == null || correct == null) return false;

        user = user.toLowerCase().trim();
        correct = correct.toLowerCase().trim();

        String[] correctWords = correct.split("\\s+");
        int matchCount = 0;

        for (String word : correctWords) {
            if (user.contains(word)) {
                matchCount++;
            }
        }

        // at least 60% keywords must match
        double ratio = (double) matchCount / correctWords.length;

        return ratio >= 0.6;
    }

            public static void main(String[] args) {
                launch(args);
            }

        }

