package mainPKG;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

public class Main {

    public static void execute(File inputFile, String outputDirPath) throws FileNotFoundException {

        File outputFile = new File(outputDirPath + "/output.html");
        File parentDir = new File(outputDirPath);
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        try {
            if (!outputFile.exists()) {
                if (!outputFile.createNewFile()) {
                    throw new RuntimeException("Failed to create file: " + outputFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating file: " + outputFile.getAbsolutePath(), e);
        }

        UserData data = new UserData(inputFile);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            printToHTML(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + outputFile.getAbsolutePath(), e);
        }
    }

    public static void printToHTML(UserData data, PrintWriter writer) {
        Map<String, Map<String, ArrayList<String>>> requirements = data.requirements;
        ArrayList<String> requirementTitleKeySet = data.requirementTitleKeySet;
        ArrayList<String> subRequirementKeySet = data.subRequirementKeySet;
        ArrayList<String> requirementExceptions = data.requirementExceptions;

        setHeader(data, writer);
        addTextHeader(data, writer);

        writer.println("<dl>");

        writer.println("<dd><dl>");
        writer.println("<div class=\"user-grid\">");

        addCreditsComplete(data, writer);
        addInProgressClasses(data, writer);
        addCompleteClasses(data, writer);

        writer.println("</div>");
        writer.println("</dl></dd>");

        for (String reqKey : requirementTitleKeySet) {
            if (requirements.containsKey(reqKey)) {
                Map<String, ArrayList<String>> subReqs = requirements.get(reqKey);

                if (!subReqs.isEmpty() ) {
                    writer.println("<dt class=\"req container block-animation\">" + reqKey + "</dt>");
                }
                writer.println("<dd><dl>");

                writer.println("<div class=\"sub-grid\">");
                for (String subReqKey : subRequirementKeySet) {
                    if (subReqs.containsKey(subReqKey) && !subReqKey.trim().isEmpty() && !subReqKey.contains("COMPLETED")) {
                        ArrayList<String> classes = subReqs.get(subReqKey);

                        String expand = "";
                        if (classes.size() >= 10) {
                            expand = " span-rest";
                        }
                        writer.println("<div class=\"view"  + expand + "\">");
                        writer.println("<div class=\"container block-animation\">");
                        writer.println("<dt class =\"sub-req\">" + subReqKey.trim() + "</dt>");

                        StringBuilder combo = new StringBuilder();
                        for (String cls : classes) {
                            if (!cls.trim().isEmpty()) {
                                combo.append(cls);
                                combo.append(" ");
                            }
                        }

                        String cls = combo.toString();
                        if (!combo.isEmpty()) {
                            cls = addLinks(combo.toString());
                        }
                        if (cls.contains(",")) {
                            cls = cls.replaceAll(",", "");
                        }
                        writer.println("<dd class =\"class-title\">" + cls + "</dd>");
                        writer.println("</div>");
                        writer.println("</div>");
                    }
                }

                writer.println("</div>");
                writer.println("</dl></dd>");
            }
        }
        writer.println("</dl>");

        if (!requirementExceptions.isEmpty()) {
            writer.println("<div class=\"exceptions-box\">");
            writer.println("<h3>OTHER REQUIREMENTS</h3>");
            for (String exception : requirementExceptions) {
                writer.println("<p>" + exception + "</p>");
            }
            writer.println("</div>");
        }

        writer.println("<div class=\"footer\">");
        writer.println("</div>");
        writer.println("</body>");
        writer.println("</html>");

        writer.flush();
    }

    public static void setHeader(UserData data, PrintWriter writer) {
        int total = 720;
        double percentComplete = (data.creditsComplete / (data.creditsComplete + data.creditsNeeded + data.creditsInProgress));
        int fill = (int) (total - (total * percentComplete));

        writer.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">");

        writer.println("<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + data.name + " Degree Audit</title>");

        //TOTAL - (TOTAL * DECIMAL)
        writer.println("    <style>\n" +
                "        circle {\n" +
                "            fill: none;\n" +
                "            stroke: url(#GradientColor);\n" +
                "            stroke-width: 20px;\n" +
                "            stroke-dasharray: 720;\n" +
                "            stroke-dashoffset: 720;\n" +
                "            animation: anim 0.5s linear forwards;\n" +
                "        }\n" + "        @keyframes anim {\n" +
                        "            100% {\n" +
                        "                stroke-dashoffset: " + fill + ";\n" +
                        "            }\n" +
                        "        }" +
                "    </style>");

        writer.println("<link rel=\"stylesheet\" href=\"../stylesheet.css\">");

        writer.println("</head>");
        writer.println("<body>");
    }
    public static void addTextHeader(UserData data, PrintWriter writer) {
        String name = data.name;
        String major = data.major;
        String studentID = data.studentID;
        String date = data.date;

        // java.Main heading
        writer.println("<div class=\"osu-header\">");
        writer.println("<div class=\"header-text\">");
        writer.println("<h1>" + name + "</h1>");
        writer.println("<h2>" + major + "</h2>");
        writer.println("<h3>" + studentID + "</h3>");
        writer.println("<h3>" + date + "</h3>");
        writer.println("</div>");
        writer.println("<img src=\"../Resources/block-o-header.png\" class=\"block-o\">");
        writer.println("<p class=\"home-button\"><a href=\"../\">HOME</a></p>");
        writer.println("</div>");
    }
    public static void addCreditsComplete(UserData data, PrintWriter writer) {
        double totalCredits = (data.creditsComplete + data.creditsNeeded + data.creditsInProgress);
        int percentComplete = (int) ((data.creditsComplete / (totalCredits)) * 100);

        writer.println("<div class=\"view\">");
        writer.println("<div class=\"container block-animation\">");
        writer.println("<dt class=\"user-stats\">CREDITS COMPLETED - " + data.creditsComplete + " / " + totalCredits + "</dt>");
        writer.println("                        <dt class=\"credits-box\">\n" +
                "                            <div class=\"outer\">\n" +
                "                                <div class=\"inner\">\n" +
                "                                    <div id=\"number\">\n" +
                "                                        " + percentComplete + "%\n" +
                "                                    </div>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "\n" +
                "                            <svg height=\"250px\" version=\"1.1\" width=\"250px\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "                                <defs>\n" +
                "                                    <linearGradient id=\"GradientColor\">\n" +
                "                                        <stop offset=\"0%\" stop-color=\"red\"/>\n" +
                "                                        <stop offset=\"100%\" stop-color=\"red\"/>\n" +
                "                                    </linearGradient>\n" +
                "                                </defs>\n" +
                "                                <circle cx=\"125\" cy=\"125\" r=\"115\" stroke-linecap=\"round\"/>\n" +
                "                            </svg>\n" +
                "                        </dt>");
        writer.println("</div>");
        writer.println("</div>");
    }
    public static void addInProgressClasses (UserData data, PrintWriter writer) {
        Set<Course> inProgressClasses = new HashSet<>(data.inProgressClasses);

        writer.println("<div class=\"view\">");
        writer.println("<div class=\"container block-animation\">");
        writer.println("<dt class=\"user-stats\">CLASSES IN PROGRESS - " +  data.creditsInProgress + "</dt>");
        for (Course curr : inProgressClasses) {
            writer.println("<dd class=\"in-progress\">" + addLinks(curr.courseName) + " " + curr.creditHours + "</dd>");
        }
        writer.println("</div>");
        writer.println("</div>");
    }
    public static void addCompleteClasses (UserData data, PrintWriter writer) {
        Set<Course> completedClasses = new HashSet<>(data.completedClasses);

        writer.println("<div class=\"view span-two\">");
        writer.println("<div class=\"container-complete block-animation\">");
        writer.println("<dt class=\"user-stats span-rest\">CLASSES COMPLETE - " + data.creditsComplete + "</dt>");
        for (Course curr : completedClasses) {
            writer.println("<dd class=\"complete\">" + curr.courseName + " " + curr.creditHours + " "+ curr.grade + "</dd>");
        }
        writer.println("</div>");
        writer.println("</div>");
    }

    public static String addLinks(String line) {
        StringBuilder result = new StringBuilder();
        String[] parts = line.split("\\s+");
        String baseUrl = "https://classes.osu.edu/#/?q=";
        String urlSuffix = "&client=class-search-ui&campus=col&term=1248&p=1%23top-nav";
        String currentAbbrev = "";

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("OR")) {
                result.append(" OR ");
                continue;
            }

            if (Character.isLetter(parts[i].charAt(0))) {
                currentAbbrev = parts[i];
            } else if (Character.isDigit(parts[i].charAt(0))) {
                String[] numbers = parts[i].split(",");
                for (int j = 0; j < numbers.length; j++) {
                    String num = numbers[j].trim();
                    appendCourseLink(result, currentAbbrev, num, baseUrl, urlSuffix);
                    if (j < numbers.length - 1) {
                        result.append(", ");
                    }
                }
                if (i < parts.length - 1 && !parts[i + 1].equals("OR")) {
                    result.append(" ");
                }
            } else {
                result.append(parts[i]).append(" ");
            }
        }

        return result.toString().trim();
    }

    private static void appendCourseLink(StringBuilder result, String abbrev, String num, String baseUrl, String urlSuffix) {
        String fullName = abbrev + " " + num;
        result.append(String.format("<a href=\"%s%s%%20%s%s\" target=\"_blank\">%s</a>",
                baseUrl, abbrev, num, urlSuffix, fullName));
        //result.append("</br >");
    }

}