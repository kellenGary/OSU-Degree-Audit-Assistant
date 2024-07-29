package mainPKG;

import java.io.*;
import java.util.*;

public class UserData {

    public String name,
            major,
            studentID,
            date;
    public double creditsInProgress, creditsComplete, creditsNeeded;

    public ArrayList<String> requirementTitleKeySet = new ArrayList<>();
    public ArrayList<String> subRequirementKeySet = new ArrayList<>();
    public ArrayList<String> requirementExceptions = new ArrayList<>();
    public ArrayList<Course> completedClasses = new ArrayList<>();
    public ArrayList<Course> inProgressClasses = new ArrayList<>();

    public Map<String, Map<String, ArrayList<String>>> requirements = new HashMap<>();

    public UserData(File inputFile) {

        BufferedReader fileReader = null;

        try {
            fileReader = new BufferedReader(new FileReader(inputFile));
            readFile(fileReader);
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException("File not found: " + inputFile.getAbsolutePath(), e);
        }

        //printAll();
    }

    public void printAll() {
        System.out.println("NAME: " + name);
        System.out.println("MAJOR: " + major);
        System.out.println("DATE: " + date);
        System.out.println("STUDENT ID: " + studentID);

        for (Map.Entry<String, Map<String, ArrayList<String>>> entry : requirements.entrySet()) {
            String reqTitle = entry.getKey();
            Map<String, ArrayList<String>> subReqs = entry.getValue();

            System.out.println("Requirement Title: " + reqTitle);
            for (Map.Entry<String, ArrayList<String>> subReq : subReqs.entrySet()) {
                String subReqTitle = subReq.getKey();
                ArrayList<String> classes = subReq.getValue();

                if (subReqTitle != null && !subReqTitle.trim().isEmpty()) {
                    System.out.println("\tSub Requirement Title: " + subReqTitle);
                    for (String cls : classes) {
                        System.out.println("\t\t" + cls);
                    }
                }
            }
        }
    }

    private void readFile(BufferedReader fileReader) {

        try {
            getName(fileReader);
            getMajor(fileReader);
            getDate(fileReader);
            getStudentID(fileReader);
            getReqTitles(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getReqTitles(BufferedReader fileReader) throws IOException {
        boolean isCompleted = true;
        boolean isSubName = false;
        ArrayList<String> classes = new ArrayList<>();
        Map<String, ArrayList<String>> subReqs = new HashMap<>();

        String req = " ";
        StringBuilder subReqName = new StringBuilder();

        String line;
        while ((line= fileReader.readLine()) != null) {

            // GET STATUS
            if (line.contains("<div class=\"status statusNO\" title=\"Requirement Unfulfilled\">Requirement Unfulfilled</div>")) {
                isCompleted = false;
            }

            if (!isCompleted) {
                // GET REQ NAME
                if (line.contains("<div class=\"reqTitle\">")) {
                    req = formatReq(line);
                    subReqs = new HashMap<>();
                }

                // GET CLASSES
                if (line.contains("<span class=\"status Status_NO\" title=\"Sub-Requirement Unfulfilled\">Sub Requirement Unfulfilled</span>")
                        || line.contains("<span class=\"status Status_NONE\" title=\"Sub-Requirement \">Sub Requirement </span>")) {
                    while ((line= fileReader.readLine()) != null) {

                        if (line.contains("<div class=\"reqHeaderTable\">")) {
                            break;
                        }

                        if (line.contains("<span class=\"subreqTitle\">")) {
                            isSubName = true;
                            subReqName.setLength(0);
                            classes = new ArrayList<>();
                        } else if (line.contains("</span>") && isSubName) {
                            isSubName = false;
                            subReqs.put(formatSubReq(subReqName.toString()), classes);
                            subRequirementKeySet.add(formatSubReq(subReqName.toString()));
                        }
                        if (isSubName) {
                            subReqName.append(line);
                        }

                        if (line.contains("<td class=\"fromcourselist\">")) {
                            String currClass = formatClass(line);
                            if (!currClass.trim().isEmpty()) {
                                classes.add(currClass);
                            }
                        }
                    }

                    if (!subReqName.toString().trim().isEmpty() && !req.contains("** ADVISORS **")) {
                        requirements.put(req, subReqs);
                        requirementTitleKeySet.add(req);
                    } else if (!req.contains("** ADVISORS **")){
                        requirementExceptions.add(req);
                    }

                    isCompleted = true;
                }

            }
            if (line.contains("<tr class=\"takenCourse \">")) {
                Course course = getCourseInfo(fileReader);
                if (!completedClasses.contains(course)) {
                    completedClasses.add(course);
                }
            } else if (line.contains("<tr class=\"takenCourse ip\">")) {
                Course course = getCourseInfo(fileReader);
                if (!inProgressClasses.contains(course)) {
                    inProgressClasses.add(course);
                }
            }

            if (line.contains("REQUIRED FOR GRADUATION")) {
                getCreditsInfo(fileReader);
            }

        }
    }

    public void getCreditsInfo(BufferedReader fileReader) throws IOException {
        String line;
        while ((line= fileReader.readLine()) != null) {
            if (line.contains("EARNED:")) {
                while ((line= fileReader.readLine()) != null) {
                    if (line.contains("<span class=\"hours number\">")) {
                        creditsComplete = Double.parseDouble(line.replaceAll("<[^>]*>", "").trim());
                        break;
                    }
                }
            }
            if (line.contains("IN-PROGRESS:")) {
                while ((line= fileReader.readLine()) != null) {
                    if (line.contains("<span class=\"hours number\">")) {
                        creditsInProgress = Double.parseDouble(line.replaceAll("<[^>]*>", "").trim());
                        break;
                    }
                }
            }
            if (line.contains("NEEDS:")) {
                while ((line= fileReader.readLine()) != null) {
                    if (line.contains("<span class=\"hours number\">")) {
                        creditsNeeded = Double.parseDouble(line.replaceAll("<[^>]*>", "").trim());
                        break;
                    }
                }
                break;
            }
        }
    }

    public Course getCourseInfo(BufferedReader fileReader) throws IOException {
        Course course = null;
        String name = "";
        String grade = "";
        String creditHours = "";
        String line;
        while ((line= fileReader.readLine()) != null) {

            if (line.contains("<td class=\"course\">")) {
                name = line.replaceAll("<[^>]*>", "");
                name = name.replaceAll("\\s+", " ").trim();
            }
            else if (line.contains("<td class=\"credit\">")) {
                creditHours = line.replaceAll("<[^>]*>", "").trim();
            }
            else if (line.contains("<td class=\"grade\">")) {
                grade = line.replaceAll("<[^>]*>", "").trim();
                return new Course(name, grade, creditHours);
            }
        }
        return course;
    }

    public String formatReq(String line) {
        line = line.substring(line.indexOf(">") + 1, line.indexOf("</div>"));
        line = line.replaceAll("\\s+", " ").trim();
        line = line.replaceAll("<br />", " ");

        return line;
    }

    public String formatSubReq(String line) {
        line = line.replaceAll("<span class=\"subreqTitle\">", " ");
        line = line.replaceAll("\\*", "");
        line = line.replaceAll("<br />", " ");
        return line;
    }

    public String formatClass(String line) {
        String cut1 = "<tr><td class=\"fromcourselist\">";
        line = line.substring(line.indexOf(cut1) + cut1.length());
        line = line.replaceAll("<[^>]*>", "");
        line = line.replaceAll(",", ", ");
        line = line.replaceAll("\\s+", " ").trim();

        if (line.trim().endsWith(",")) {
            line = line.substring(0, line.lastIndexOf(","));
        }
        if (line.contains("AU24 OR AFTER")) {
            line = line.replaceAll("\\(AU24 OR AFTER\\)", "");
        }
        if (line.contains("SP25")) {
            line = line.replaceAll("\\(SP25", "");
        }

        return line;
    }

    private void getName(BufferedReader fileReader) throws IOException {
        String target = "<!--div -->";
        String line;
        while ((line= fileReader.readLine()) != null) {
            if (line.contains(target)) {
                this.name = fileReader.readLine().trim();
                break;
            }
        }
    }

    private void getMajor (BufferedReader fileReader) throws IOException {
        String targetDiv = "<!-- /div -->";
        boolean foundTargetDiv = false;
        StringBuilder major = new StringBuilder();

        String line;
        while ((line= fileReader.readLine()) != null) {
            if (foundTargetDiv) {
                if (line.contains("</div>")) {
                    this.major =  major.toString().replace("<br/>", "").trim();
                    if (this.major.startsWith("<br>")) {
                        this.major = this.major.replace("<br>", "");
                    }
                    break;
                }
                if (!line.startsWith("<")) {
                    if (!major.isEmpty()) {
                        major.append(" ");
                    }
                    major.append(line.trim());
                }
            } else if (line.contains(targetDiv)) {
                foundTargetDiv = true;
            }
        }
    }

    private void getDate (BufferedReader fileReader) throws IOException {
        String line;
        while((line= fileReader.readLine()) != null) {
            if (line.contains("<th>Prepared On</th>")) {
                line = line.replaceAll("<[^>]*>", " ").trim();
                line = line.replaceAll("\\s+", " ").trim();
                this.date = line.trim();
                break;
            }
        }
    }

    private void getStudentID (BufferedReader fileReader) throws IOException {
        String line;
        while((line= fileReader.readLine()) != null) {
            if (line.contains("<th>Student ID</th>")) {
                line = line.replaceAll("<[^>]*>", " " ).trim();
                this.studentID = line.trim();
                break;
            }
        }


    }
}