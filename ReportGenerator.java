import java.io.FileWriter;
import java.io.File;

public class ReportGenerator {

    public static void generateReport(
            String name,
            int score,
            int total,
            double percent,
            String result,
            int warnings
    ) {

        try {
            System.out.println(">>> REPORT METHOD CALLED");

            // Save in Desktop for easy access
            String filePath = System.getProperty("user.home")
                    + File.separator + "Desktop"
                    + File.separator + name + "_report.txt";
            System.out.println("DEBUG: Writing file to -> " + filePath);

            File file = new File(filePath);

            FileWriter fw = new FileWriter(file);

            fw.write("=================================\n");
            fw.write("        ONLINE EXAM REPORT        \n");
            fw.write("=================================\n\n");

            fw.write("Student Name   : " + name + "\n");
            fw.write("Score          : " + score + "/" + total + "\n");
            fw.write("Percentage     : " + percent + "%\n");
            fw.write("Result         : " + result + "\n");
            fw.write("Warnings       : " + warnings + "\n");

            fw.write("\n---------------------------------\n");

            if (warnings >= 3) {
                fw.write("STATUS: AUTO SUBMITTED (VIOLATIONS DETECTED)\n");
            } else {
                fw.write("STATUS: NORMAL SUBMISSION\n");
            }

            fw.write("=================================\n");

            fw.close();

            System.out.println("Report generated at: " + filePath);
            System.out.println("✅ REPORT CREATED SUCCESSFULLY");

        } catch (Exception e) {
            System.out.println("❌ REPORT GENERATION FAILED");
            e.printStackTrace();
        }
    }
}