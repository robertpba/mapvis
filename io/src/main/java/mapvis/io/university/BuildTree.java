package mapvis.io.university;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static mapvis.io.university.ParseCVS.readCSVFile;

public class BuildTree {

    public static Node build(List<Major> majors) {
        Node root = new Node();
        root.type = "University";
        root.label = "University";

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

            if (m.getProgram().equals("Doctoral"))
                major.label = "(D)";
            else if (m.getProgram().equals("Master"))
                major.label = "(M)";
            else if (m.getProgram().equals("Bachelor"))
                major.label = "(B)";
            else if (m.getProgram().equals("Postgraduate Certificate/Diploma"))
                major.label = "(P)";
            major.label += m.getMajor().replaceAll(" \\([A-Za-z \\-/0-9]+\\)","");
            major.size = m.getTotal();
            department.children.add(major);
       });

        populateSize(root);
        return  root;
    }

    static void populateSize(Node node){
        for (Node child : node.children) {
            populateSize(child);
        }
        if (!node.children.isEmpty())
            node.size = node.children.stream().mapToInt(c->c.size).sum();
    }


    public static void main(String[] args) throws IOException {
        List<Major> majors = readCSVFile("data/Student numbers.csv");
        Node root = build(majors);
        System.out.print(root);
    }

}
