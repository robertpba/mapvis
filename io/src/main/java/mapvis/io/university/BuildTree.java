package mapvis.io.university;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static mapvis.io.university.ParseCVS.readCSVFile;

public class BuildTree {

    public static Node build(List<Major> majors) {
        Node root = new Node();

        HashMap<String, Node> faculties = new HashMap<>();
        HashMap<String, Node> dummyDepartments = new HashMap<>();
        HashMap<String, Node> departments = new HashMap<>();

        majors.stream().map(Major::getFaculty).distinct()
                .forEach(name -> {
                    Node node = new Node();
                    node.level = 1;
                    node.label = name;
                    node.type = "faculty";
                    faculties.put(name,node);
                });
        root.children.addAll(faculties.values());

        majors.stream().forEach(m->{
            Node department;
            if (m.getDepartment() == null){
                department = dummyDepartments.get(m.getFaculty());
                if (department == null){
                    department = new Node();
                    department.level = 2;
                    department.label = "";
                    department.type  = "department";
                    dummyDepartments.put(m.getFaculty(), department);

                    Node faculty = faculties.get(m.getFaculty());
                    faculty.children.add(department);
                }
            } else {
                department = departments.get(m.getDepartment());
                if (department == null){
                    department = new Node();
                    department.level = 2;
                    department.label = m.getDepartment();
                    department.type  = "department";
                    departments.put(m.getDepartment(), department);

                    Node faculty = faculties.get(m.getFaculty());
                    faculty.children.add(department);
                }
            }

            Node major = new Node();
            major.level = 3;
            major.type  = "major";
            major.label = m.getProgram() + " - " + m.getMajor();
            major.size = m.getTotal();
            department.children.add(major);
       });

        return  root;
    }

    public static void main(String[] args) throws IOException {
        List<Major> majors = readCSVFile("data/Student numbers.csv");
        Node root = build(majors);
        System.out.print(root);
    }

}
