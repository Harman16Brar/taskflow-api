//package com.taskflow_api.user;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//class Project {
//    private String projectName;
//    private String client;
//    private int durationMonths;
//    private double budget;
//}
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//class Employee {
//    private int id;
//    private String name;
//    private String department;
//    private String designation;
//    private double salary;
//    private int age;
//    private boolean active;
//    private String location;
//    private List<String> skills;
//    private List<Project> projects;
//}
//
//public class Main {
//    public static void main(String[] args) {
//        List<Employee> employees = List.of(
//                new Employee(
//                        1, "Aman", "IT", "Senior Developer", 120000, 29, true, "Delhi",
//                        List.of("Java", "Spring", "Kafka"),
//                        List.of(
//                                new Project("Phoenix", "Google", 12, 200000),
//                                new Project("Atlas", "Amazon", 8, 150000)
//                        )
//                ),
//                new Employee(
//                        2, "Neha", "HR", "HR Manager", 90000, 35, true, "Bangalore",
//                        List.of("Hiring", "Communication"),
//                        List.of(
//                                new Project("HiringDrive", "Internal", 6, 50000)
//                        )
//                ),
//                new Employee(
//                        3, "Rohit", "IT", "Architect", 180000, 40, false, "Delhi",
//                        List.of("Java", "Microservices", "AWS"),
//                        List.of(
//                                new Project("Phoenix", "Google", 12, 200000),
//                                new Project("Nimbus", "Microsoft", 15, 300000)
//                        )
//                ),
//                new Employee(
//                        4, "Simran", "Finance", "Analyst", 95000, 31, true, "Mumbai",
//                        List.of("Excel", "Accounting"),
//                        List.of(
//                                new Project("FinEdge", "Goldman", 10, 120000)
//                        )
//                ),
//                new Employee(
//                        5, "Kunal", "IT", "Developer", 85000, 26, true, "Bangalore",
//                        List.of("Java", "React"),
//                        List.of(
//                                new Project("Atlas", "Amazon", 8, 150000)
//                        )
//                ),
//                new Employee(
//                        6, "Priya", "IT", "Developer", 85000, 26, true, "Pune",
//                        List.of("Java", "React"),
//                        List.of() // No projects
//                )
//        );
//
//        // =====================================================================
//        // PHASE 1: INTERMEDIATE PIPELINE OPERATIONS (LAZY METHODS)
//        // =====================================================================
//
//        // ⚡ Question 1: Filtering & Extraction (filter & map)
//        // Requirement: Extract the names of all employees who work in the "IT" department.
//        // Expected Output: ["Aman", "Rohit", "Kunal", "Priya"]
//
//        List<String> itEmployeeNames = employees.stream()
//                .filter((employee -> "IT".equals(employee.getDepartment())))
//                .map(Employee::getName)
//                .collect(Collectors.toList());
//
//        List<String> itEmployeeNames1 = employees.stream()
//                .filter((employee -> "IT".equals(employee.getDepartment())))
//                .map(Employee::getName)
//                .toList(); //unmodifiable list.
//
//        //Interview Question 2:
//        // Nested Data Flattening (flatMap)📝
//        // Problem StatementExtract a single,
//        // flat list of every single Project assigned across the entire company.
//        // Your stream pipeline must traverse each employee's nested project collection and
//        // merge them all into a single stream.📦
//        // Expected Output:
//        // A list containing 7 project objects total
//        // (Aman's 2, Neha's 1, Rohit's 2, Simran's 1, Kunal's 1, Priya's 0).
//
//        List<Project> allCompanyProjects = employees.stream()
//                // 1. Flatten the nested list of projects into a single stream
//                .flatMap(employee -> employee.getProjects() == null ? Stream.empty() : employee.getProjects().stream())
//                // 2. Collect to a list using modern syntax
//                .toList();
//
//        System.out.println("Q2 Output Count: " + allCompanyProjects.size());
//
//        // Interview Question 3:
//        // Deduplication & Ordering (distinct & sorted)📝
//        // Problem Statement:
//        // Get a unique list of employee ages, sorted in descending order (oldest to youngest).
//        // Your solution must extract the age field, eliminate any duplicate ages, and sort them backwards.📦
//        // Expected Output: [40, 35, 31, 29, 26]
//
//        List<Integer> sortedUniqueAges = employees.stream()
//                // 1. Map Employee to Age
//                .map(Employee::getAge)
//                .distinct()
//                .sorted(Comparator.reverseOrder())
//                .toList();
//
//
//        //.sorted(Comparator.naturalOrder())
//        //.sorted(Comparator.reverseOrder())
//
//        System.out.println("Q3 Output: " + sortedUniqueAges);
//
//
//
//
//    }
//}
