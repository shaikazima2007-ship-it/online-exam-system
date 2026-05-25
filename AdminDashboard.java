import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.FileWriter;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class AdminDashboard extends Application {
    private TableView<StudentResult> table = new TableView<>();
    private ObservableList<StudentResult> data = FXCollections.observableArrayList();


    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/Online_db";

    private static final String USER = "root";

    private static final String PASS = "Azima@2007";

    // ---------------- MODEL CLASS ----------------
    public static class StudentResult {

        private final SimpleStringProperty username;

        private final SimpleIntegerProperty score;

        private final SimpleIntegerProperty total;

        private final SimpleDoubleProperty percentage;

        private final SimpleStringProperty result;

        private final SimpleIntegerProperty violations;

        private final SimpleIntegerProperty rank;

        private final SimpleStringProperty examDate;

        private final SimpleStringProperty cheating;
        private ObservableList<StudentResult> masterData = FXCollections.observableArrayList();

        public StudentResult(
                String username,
                int score,
                int total,
                double percentage,
                String result,
                int violations,
                String cheating,
                int rank,
                String examDate
        ) {

            this.username =
                    new SimpleStringProperty(username);

            this.score =
                    new SimpleIntegerProperty(score);

            this.total =
                    new SimpleIntegerProperty(total);

            this.percentage =
                    new SimpleDoubleProperty(percentage);

            this.result =
                    new SimpleStringProperty(result);

            this.violations =
                    new SimpleIntegerProperty(violations);

            this.rank =
                    new SimpleIntegerProperty(rank);

            this.examDate =
                    new SimpleStringProperty(examDate);

            this.cheating = new SimpleStringProperty(cheating);
        }


        public String getUsername() {
            return username.get();
        }

        public int getScore() {
            return score.get();
        }

        public int getTotal() {
            return total.get();
        }

        public double getPercentage() {
            return percentage.get();
        }

        public String getResult() {
            return result.get();
        }

        public int getViolations() {
            return violations.get();
        }

        public int getRank() {
            return rank.get();
        }

        public String getExamDate() {
            return examDate.get();
        }

        public String getCheating() {
            return cheating.get();
        }
    }

    // ---------------- START ----------------
    @Override
    public void start(Stage stage) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search student...");

        setupSearch(searchField);

        Label title =
                new Label(
                        "REAL-TIME EXAM MONITORING DASHBOARD"
                );
        Button downloadBtn =
                new Button("Download Report");

        Button addBtn =
                new Button("Add Question");

        Button editBtn =
                new Button("Edit Question");

        Button deleteBtn =
                new Button("Delete Question");

        Button examBtn =
                new Button("Manage Exam");

        Button studentsBtn =
                new Button(
                        "View Students"
                );

        title.setStyle(
                "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#2c3e50;"
        );

        // ---------------- TABLE ----------------


        TableColumn<StudentResult, String> userCol =
                new TableColumn<>("Student");

        userCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getUsername()
                )
        );

        TableColumn<StudentResult, Number> scoreCol =
                new TableColumn<>("Score");

        scoreCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getScore()
                )
        );

        TableColumn<StudentResult, Number> totalCol =
                new TableColumn<>("Total");

        totalCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getTotal()
                )
        );

        TableColumn<StudentResult, Number> percentCol =
                new TableColumn<>("Percentage");

        percentCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getPercentage()
                )
        );

        TableColumn<StudentResult, String> resultCol =
                new TableColumn<>("Result");
        TableColumn<StudentResult, String> cheatCol =
                new TableColumn<>("Cheating");
        cheatCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCheating())
        );

        TableColumn<StudentResult, Number> violationCol =
                new TableColumn<>("Violations");

        violationCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getViolations()
                )
        );

        TableColumn<StudentResult, Number> rankCol =
                new TableColumn<>("Rank");

        rankCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getRank()
                )
        );

        TableColumn<StudentResult, String> dateCol =
                new TableColumn<>("Date & Time");

        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getExamDate()
                )
        );

        resultCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getResult())
        );

        resultCol.setCellFactory(col -> new TableCell<StudentResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (item.equalsIgnoreCase("PASS")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("FAIL")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });


        table.getColumns().addAll(
                rankCol,
                userCol,
                scoreCol,
                totalCol,
                percentCol,
                violationCol,
                resultCol,
                dateCol,
                cheatCol
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        table.setRowFactory(tv -> {
            TableRow<StudentResult> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && !row.isEmpty()) {

                    StudentResult selected = row.getItem();

                    ButtonType studentDownloadBtn =
                            new ButtonType(
                                    "Download Report"
                            );

                    ButtonType closeBtn =
                            new ButtonType(
                                    "Close",
                                    ButtonBar.ButtonData.CANCEL_CLOSE
                            );

                    Alert alert =
                            new Alert(
                                    Alert.AlertType.INFORMATION
                            );

                    alert.setTitle("Student Result");

                    String message =
                            "Score: " + selected.getScore() +
                                    "\nPercentage: " + selected.getPercentage() +
                                    "\nResult: " + selected.getResult() +
                                    "\nViolations: " + selected.getViolations();

                    // PASS / FAIL popup

                    if(selected.getResult().equalsIgnoreCase("PASS")){

                        message +=
                                "\n\n🎉 Congratulations! Keep it up.";

                    }
                    else{

                        message +=
                                "\n\n⚠ Don't repeat next time. Work harder.";

                    }

                    alert.setHeaderText(
                            selected.getUsername()
                    );

                    alert.setContentText(message);
                    ButtonType close =
                            new ButtonType(
                                    "Close"
                            );

                    alert.getButtonTypes().setAll(
                            studentDownloadBtn,
                            close
                    );

                    alert.showAndWait().ifPresent(btn -> {

                        if(btn==studentDownloadBtn){

                            try{

                                File file =
                                        new File(
                                                System.getProperty("user.home")
                                                        + "/Desktop/"
                                                        + selected.getUsername()
                                                        + "_Report.txt"
                                        );

                                FileWriter fw =
                                        new FileWriter(file);

                                fw.write("====================================\n");
                                fw.write("ONLINE EXAM SYSTEM - STUDENT REPORT\n");
                                fw.write("====================================\n\n");

                                fw.write("Student Name : " + selected.getUsername() + "\n");
                                fw.write("Score        : " + selected.getScore() + "\n");
                                fw.write("Percentage   : " + selected.getPercentage() + "%\n");
                                fw.write("Result       : " + selected.getResult() + "\n");
                                fw.write("Violations   : " + selected.getViolations() + "\n");
                                fw.write("Exam Date    : " + selected.getExamDate() + "\n");

                                fw.write("\n------------------------------------\n");

                                if(selected.getResult().equalsIgnoreCase("PASS")){

                                    fw.write("CONGRATULATIONS 🎉\n");
                                    fw.write("Excellent performance in examination.\n");
                                    fw.write("Keep learning and keep growing.\n");

                                }
                                else{

                                    fw.write("IMPROVE NEXT TIME ⚠\n");
                                    fw.write("Work on weak areas and practice more.\n");

                                }

                                if(selected.getViolations()>0){

                                    fw.write("\nViolations Recorded : YES\n");
                                    fw.write("Exam behaviour monitoring detected issues.\n");

                                }
                                else{

                                    fw.write("\nViolations Recorded : NO\n");

                                }

                                if(selected.getViolations()>=3){

                                    fw.write("\nEXAM STATUS : LOCKED\n");
                                    fw.write("Exam terminated due to rule violations.\n");

                                }
                                else{

                                    fw.write("\nEXAM STATUS : COMPLETED\n");

                                }

                                fw.write("\nGenerated by Admin Dashboard\n");
                                fw.write("====================================\n");
                                fw.close();

                                Alert ok =
                                        new Alert(
                                                Alert.AlertType.INFORMATION
                                        );

                                ok.setContentText(
                                        "Downloaded to Desktop"
                                );

                                ok.show();

                            }
                            catch(Exception ex){

                                ex.printStackTrace();

                            }

                        }

                    });

                }

            });

            return row;
        });

        // ---------------- LOAD DATA ----------------
        loadTable(table);
        downloadBtn.setOnAction(e -> {

            generateReport();

        });
        addBtn.setOnAction(e -> addQuestion());

        editBtn.setOnAction(e -> editQuestion());

        deleteBtn.setOnAction(e -> deleteQuestion());

        examBtn.setOnAction(e -> manageExam());

        studentsBtn.setOnAction(e -> viewStudents());


        // ---------------- AUTO REFRESH ----------------
        Timeline refresh = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {

                    if (searchField.getText() == null || searchField.getText().isEmpty()) {
                        loadTable(table);
                    }

                })
        );

        // ---------------- LAYOUT ----------------
        HBox topBar = new HBox(10, searchField, downloadBtn, addBtn, editBtn, deleteBtn, examBtn, studentsBtn);

        VBox layout = new VBox(20, title, topBar, table);

        layout.setPadding(
                new Insets(20)
        );

        Scene scene =
                new Scene(layout, 900, 500);

        stage.setScene(scene);

        stage.setTitle("Admin Dashboard");

        stage.show();
    }

    // ---------------- LOAD TABLE ----------------
    // ---------------- LOAD TABLE ----------------
    private void loadTable(TableView<StudentResult> table) {

        data.clear();

        int previousPercentage = -1;
        int realRank = 0;
        int rowCount = 0;
        String sql =
                "SELECT * FROM resultss " +
                        "ORDER BY percentage DESC";

        try (

                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                USER,
                                PASS
                        );

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)

        ) {


            while (rs.next()) {


                int violations = 0;


                int currentPercentage =
                        (int) rs.getDouble("percentage");

                if (currentPercentage != previousPercentage) {

                    realRank++;

                    previousPercentage =
                            currentPercentage;
                }


                try {

                    String vsql =
                            "SELECT violation_no " +
                                    "FROM violations " +
                                    "WHERE student=? " +
                                    "ORDER BY id DESC LIMIT 1";
                    PreparedStatement ps =
                            conn.prepareStatement(vsql);

                    ps.setString(
                            1,
                            rs.getString("username")
                    );

                    ResultSet vrs =
                            ps.executeQuery();

                    if (vrs.next()) {

                        violations = vrs.getInt("violation_no");
                    }

                } catch (Exception ex) {

                    violations = 0;
                }
                String cheatingLevel;

                if (violations <= 1) {
                    cheatingLevel = "LOW";
                } else if (violations <= 2) {
                    cheatingLevel = "HIGH";
                } else if (violations >= 3) {
                    cheatingLevel = "CRITICAL";
                } else {
                    cheatingLevel = "LOW";
                }

                data.add(new StudentResult(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getDouble("percentage"),
                        rs.getString("result"),
                        violations,
                        cheatingLevel,
                        realRank,
                        rs.getTimestamp("exam_date").toString()
                ));
            }

            table.setItems(data);
            table.refresh();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void generateReport() {

        String sql =
                "SELECT * FROM resultss " +
                        "ORDER BY percentage DESC";

        try (

                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                USER,
                                PASS
                        );

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)

        ) {


            File file =
                    new File(System.getProperty("user.home") + "/Desktop/Admin_Report.txt");
            System.out.println(file.getAbsolutePath());

            FileWriter fw =
                    new FileWriter(file);

            fw.write(
                    "\n");
            fw.write(
                    "                ONLINE EXAM SYSTEM\n");

            fw.write(
                    "             ADMIN PERFORMANCE REPORT\n");

            fw.write(
                    "============================================================\n");

            fw.write(
                    String.format(
                            "%-6s %-15s %-10s %-10s %-10s %-10s\n",
                            "Rank",
                            "Student",
                            "Score",
                            "Percent",
                            "Violations",
                            "Result"
                    ));

            fw.write(
                    "============================================================\n");

            int rank = 1;
            int previousPercentage = -1;
            int realRank = 0;
            int rowCount = 0;

            while (rs.next()) {


                int currentPercentage =
                        (int) Math.round(rs.getDouble("percentage"));

                if (currentPercentage != previousPercentage) {

                    realRank++;
                    previousPercentage = currentPercentage;
                }

                int violations = 0;

                try {

                    String vsql =
                            "SELECT COUNT(*) AS v FROM violations WHERE student=?";

                    PreparedStatement ps =
                            conn.prepareStatement(vsql);

                    ps.setString(1, rs.getString("username"));

                    ResultSet vrs = ps.executeQuery();

                    if (vrs.next()) {
                        violations = vrs.getInt("v");
                    } else {
                        violations = 0;
                    }

                } catch (Exception ex) {

                    violations = 0;
                }

                String badge =
                        rs.getString("result")
                                .equalsIgnoreCase("PASS")
                                ? "PASS ✓"
                                : "FAIL ✗";

                fw.write(

                        String.format(

                                "%-6d %-15s %-10s %-10.0f %-10d %-10s\n",

                                realRank,

                                rs.getString("username"),

                                rs.getInt("score")
                                        +"/"
                                        +rs.getInt("total_questions"),

                                rs.getDouble("percentage"),

                                violations,

                                badge
                        ));

                fw.write(
                        "------------------------------------------------------------\n");

                fw.write(
                        "Exam Time : "
                                + rs.getTimestamp("exam_date")
                                +"\n");

                if(violations>0)
                {

                    fw.write(
                            "⚠ Violations Recorded : "
                                    +violations
                                    +"\n");

                }

                if(
                        rs.getString("result")
                                .equalsIgnoreCase("PASS")
                )
                {

                    fw.write(
                            "🎉 Excellent Performance\n");

                }
                else
                {

                    fw.write(
                            "📈 Needs Improvement\n");

                }

                fw.write(
                        "\n");

            }
            fw.write(
                    "============================================================\n");

            fw.write(
                    "Generated By Admin Dashboard\n");

            fw.write(
                    "Online Exam Monitoring System\n");

            fw.write(
                    "============================================================\n");

            fw.close();
            System.out.println("REPORT CREATED SUCCESSFULLY");

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Download Complete");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Report downloaded to Desktop!"
            );

            alert.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void addQuestion() {

        TextInputDialog q =
                new TextInputDialog();

        q.setHeaderText("Question");

        q.showAndWait().ifPresent(question -> {

            TextInputDialog o1 =
                    new TextInputDialog();

            o1.setHeaderText("Option 1");

            o1.showAndWait().ifPresent(op1 -> {

                TextInputDialog o2 =
                        new TextInputDialog();

                o2.setHeaderText("Option 2");

                o2.showAndWait().ifPresent(op2 -> {

                    TextInputDialog o3 =
                            new TextInputDialog();

                    o3.setHeaderText("Option 3");

                    o3.showAndWait().ifPresent(op3 -> {

                        TextInputDialog o4 =
                                new TextInputDialog();

                        o4.setHeaderText("Option 4");

                        o4.showAndWait().ifPresent(op4 -> {

                            TextInputDialog ans =
                                    new TextInputDialog();

                            ans.setHeaderText(
                                    "Correct Answer"
                            );

                            ans.showAndWait().ifPresent(answer -> {

                                try (

                                        Connection conn =

                                                DriverManager.getConnection(
                                                        URL,
                                                        USER,
                                                        PASS
                                                );

                                        PreparedStatement ps =

                                                conn.prepareStatement(

                                                        "INSERT INTO questionss(question,option1,option2,option3,option4,answer) VALUES(?,?,?,?,?,?)"

                                                )

                                ) {

                                    ps.setString(1, question);
                                    ps.setString(2, op1);
                                    ps.setString(3, op2);
                                    ps.setString(4, op3);
                                    ps.setString(5, op4);
                                    ps.setString(6, answer);

                                    ps.executeUpdate();

                                    Alert ok =
                                            new Alert(
                                                    Alert.AlertType.INFORMATION
                                            );

                                    ok.setContentText(
                                            "Question Added"
                                    );

                                    ok.show();

                                } catch (Exception ex) {

                                    ex.printStackTrace();

                                }

                            });

                        });

                    });

                });

            });

        });

    }


    private void editQuestion() {

        TextInputDialog idBox =
                new TextInputDialog();

        idBox.setHeaderText(
                "Enter Question ID"
        );

        idBox.showAndWait()
                .ifPresent(id -> {

                    TextInputDialog q =
                            new TextInputDialog();

                    q.setHeaderText(
                            "New Question"
                    );

                    q.showAndWait()
                            .ifPresent(newQ -> {

                                try (

                                        Connection conn =

                                                DriverManager.getConnection(
                                                        URL,
                                                        USER,
                                                        PASS
                                                );

                                        PreparedStatement ps =

                                                conn.prepareStatement(

                                                        "UPDATE questionss SET question=? WHERE id=?"

                                                )

                                ) {

                                    ps.setString(
                                            1,
                                            newQ
                                    );

                                    ps.setInt(
                                            2,
                                            Integer.parseInt(id)
                                    );

                                    ps.executeUpdate();

                                    Alert ok =
                                            new Alert(
                                                    Alert.AlertType.INFORMATION
                                            );

                                    ok.setContentText(
                                            "Updated"
                                    );

                                    ok.show();

                                } catch (Exception ex) {

                                    ex.printStackTrace();

                                }

                            });

                });

    }


    private void deleteQuestion() {

        TextInputDialog box =
                new TextInputDialog();

        box.setHeaderText(
                "Question ID"
        );

        box.showAndWait()
                .ifPresent(id -> {

                    try (

                            Connection conn =

                                    DriverManager.getConnection(
                                            URL,
                                            USER,
                                            PASS
                                    );

                            PreparedStatement ps =

                                    conn.prepareStatement(

                                            "DELETE FROM questionss WHERE id=?"

                                    )

                    ) {

                        ps.setInt(
                                1,
                                Integer.parseInt(id)
                        );

                        ps.executeUpdate();

                        Alert ok =
                                new Alert(
                                        Alert.AlertType.INFORMATION
                                );

                        ok.setContentText(
                                "Deleted"
                        );

                        ok.show();

                    } catch (Exception ex) {

                        ex.printStackTrace();

                    }

                });

    }

    private void manageExam() {

        Stage stage = new Stage();

        Label statusLabel =
                new Label("Exam Status");

        ComboBox<String> statusBox =
                new ComboBox<>();

        statusBox.getItems().addAll(
                "Enable",
                "Disable"
        );

        statusBox.setValue("Enable");

        Label timerLabel =
                new Label("Exam Timer (Seconds)");

        TextField timerField =
                new TextField();

        timerField.setPromptText("60");

        Label totalLabel =
                new Label("Total Questions");

        TextField totalField =
                new TextField();

        totalField.setPromptText("5");

        Button saveBtn =
                new Button("Save");

        saveBtn.setOnAction(e -> {

            try (

                    Connection conn =

                            DriverManager.getConnection(
                                    URL,
                                    USER,
                                    PASS
                            );

                    PreparedStatement ps1 =

                            conn.prepareStatement(

                                    "UPDATE exam_control SET exam_enabled=? WHERE id=1"

                            );

                    PreparedStatement ps2 =

                            conn.prepareStatement(

                                    "UPDATE exam_settings SET exam_time=?, total_questions=? WHERE id=1"

                            )

            ) {

                ps1.setBoolean(

                        1,

                        statusBox.getValue()
                                .equals("Enable")

                );

                ps1.executeUpdate();

                ps2.setInt(
                        1,
                        Integer.parseInt(
                                timerField.getText()
                        )
                );

                ps2.setInt(
                        2,
                        Integer.parseInt(
                                totalField.getText()
                        )
                );

                ps2.executeUpdate();

                Alert ok =

                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                ok.setTitle(
                        "Saved"
                );

                ok.setHeaderText(
                        null
                );

                ok.setContentText(

                        "Exam Settings Updated Successfully"

                );

                ok.showAndWait();

                stage.close();

            } catch (Exception ex) {

                ex.printStackTrace();

                Alert err =

                        new Alert(
                                Alert.AlertType.ERROR
                        );

                err.setContentText(

                        "Invalid Input"

                );

                err.show();

            }

        });

        VBox layout =
                new VBox(
                        15,

                        statusLabel,

                        statusBox,

                        timerLabel,

                        timerField,

                        totalLabel,

                        totalField,

                        saveBtn
                );

        layout.setPadding(
                new Insets(20)
        );

        Scene scene =
                new Scene(
                        layout,
                        300,
                        300
                );

        stage.setScene(scene);

        stage.setTitle(
                "Manage Exam"
        );

        stage.show();

    }

    private void viewStudents() {

        try (

                Connection conn =

                        DriverManager.getConnection(
                                URL,
                                USER,
                                PASS
                        );

                Statement st =
                        conn.createStatement();

                ResultSet rs =

                        st.executeQuery(

                                "SELECT * FROM students"

                        )

        ) {

            StringBuilder students =

                    new StringBuilder();

            while (rs.next()) {

                students.append(

                        rs.getString(
                                "username"
                        )

                ).append("\n");

            }

            Alert alert =

                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setTitle(
                    "Registered Students"
            );

            alert.setHeaderText(
                    "Student List"
            );

            alert.setContentText(

                    students.toString()

            );

            alert.showAndWait();

        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }

    private void loadCheaters() {

        data.clear();

        int realRank = 0;

        try (
                Connection conn = DriverManager.getConnection(
                        URL,
                        USER,
                        PASS
                );

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM resultss WHERE cheating_flag = 1 ORDER BY percentage DESC"
                );

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                int violations = 0;

                try {
                    String vsql =
                            "SELECT COUNT(*) AS max_v " +
                                    "FROM violations WHERE student=?";
                    PreparedStatement vps = conn.prepareStatement(vsql);
                    vps.setString(1, rs.getString("username"));

                    ResultSet vrs = vps.executeQuery();

                    if (vrs.next()) {
                        violations = vrs.getInt("max_v");
                    }

                } catch (Exception ex) {
                    violations = 0;
                }

                String cheatingLevel;

                if (violations <= 1) {
                    cheatingLevel = "LOW";
                } else if (violations <= 2) {
                    cheatingLevel = "HIGH";
                } else if (violations >= 3) {
                    cheatingLevel = "CRITICAL";
                } else {
                    cheatingLevel = "LOW";
                }

                data.add(new StudentResult(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getDouble("percentage"),
                        rs.getString("result"),
                        violations,
                        cheatingLevel,
                        realRank,
                        rs.getTimestamp("exam_date").toString()
                ));

                realRank++; // IMPORTANT
            }

            table.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCheatingLevel(int warnings) {
        if (warnings <= 1) {
            return "LOW";
        } else if (warnings == 2) {
            return "HIGH";
        } else {
            return "CRITICAL";
        }
    }

    private void addSearchFeature(TextField searchField) {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            ObservableList<StudentResult> filtered = FXCollections.observableArrayList();

            for (StudentResult s : data) {

                if (s.getUsername().toLowerCase().contains(newVal.toLowerCase())) {
                    filtered.add(s);
                }
            }

            table.setItems(filtered);
        });
    }

    private void exportSelectedStudent() {

        StudentResult selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        try {

            File file = new File(
                    System.getProperty("user.home")
                            + "/Desktop/" + selected.getUsername() + "_report.txt"
            );

            FileWriter fw = new FileWriter(file);

            fw.write("Student: " + selected.getUsername() + "\n");
            fw.write("Score: " + selected.getScore() + "\n");
            fw.write("Percentage: " + selected.getPercentage() + "\n");
            fw.write("Result: " + selected.getResult() + "\n");
            fw.write("Violations: " + selected.getViolations() + "\n");

            fw.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Report exported to Desktop");
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearch(TextField searchField) {

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal == null || newVal.isEmpty()) {
                table.setItems(data);
                return;
            }

            ObservableList<StudentResult> filtered = FXCollections.observableArrayList();

            String keyword = newVal.toLowerCase();

            for (StudentResult s : data) {

                if (s.getUsername().toLowerCase().contains(keyword)) {
                    filtered.add(s);
                }
            }

            table.setItems(filtered);
        });
    }
}
