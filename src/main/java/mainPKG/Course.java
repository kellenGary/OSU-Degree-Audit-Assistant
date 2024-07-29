package mainPKG;

import java.util.Objects;

public class Course {

    public String courseName, grade, creditHours;

    public Course(String courseName, String grade, String creditHours) {
        this.grade = grade;
        this.courseName = courseName;
        this.creditHours = creditHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseName, course.courseName);
        // Only compare courseName for equality
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseName);
        // Only use courseName for hash code calculation
    }
}
