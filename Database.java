import java.sql.*;

public class Database {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/Online_db";

    private static final String USER = "root";
    private static final String PASS = "Azima@2007";

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(

                URL,

                USER,

                PASS

        );

    }

    public static void saveResult(
            String student,
            int score,
            int total,
            double percentage,
            String result,
            int warnings
    ) {

        String sql =
                "INSERT INTO resultss (username, score, total_questions, percentage, result, cheating_flag) VALUES (?, ?, ?, ?, ?, ?)";
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, student);
            ps.setInt(2, score);
            ps.setInt(3, total);
            ps.setDouble(4, percentage);
            ps.setString(5, result);
            ps.setString(6, (warnings > 0) ? "YES" : "NO");

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("DATABASE SUCCESS");
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("DATABASE FAILED");
            e.printStackTrace();
        }
    }
    public static void saveViolation(String student, int violationNo) {

        String sql =
                "INSERT INTO violations (student, violation_no) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student);
            ps.setInt(2, violationNo);

            ps.executeUpdate();

            System.out.println("Violation saved");

        } catch (Exception e) {
            System.out.println("Failed to save violation");
            e.printStackTrace();
        }
    }
    public static boolean examEnabled(){

        try(

                Connection conn=
                        getConnection();

                Statement st=
                        conn.createStatement();

                ResultSet rs=

                        st.executeQuery(

                                "SELECT exam_enabled FROM exam_control WHERE id=1"

                        )

        ){

            if(rs.next()){

                return rs.getBoolean(
                        "exam_enabled"
                );

            }

        }

        catch(Exception e){

            e.printStackTrace();

        }

        return false;

    }
}