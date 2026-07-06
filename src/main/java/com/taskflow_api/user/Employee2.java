//package com.taskflow_api.user;
//
//import lombok.*;
//
//import java.util.*;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//class Employee2 {
//
//    private int id;
//    private String name;
//    private String department;
//    private String designation;
//    private double salary;
//    private int age;
//    private boolean active;
//    private String location;
//
//    private List<String> skills;
//    private List<Project2> Project2s;
//}
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//class Project2 {
//
//    private String Project2Name;
//    private String client;
//    private int durationMonths;
//    private double budget;
//}
//
//class Main2 {
//    public static void main(String[] args) {
//        List<Employee2> Employee2s = List.of(
//
//                new Employee2(
//                        1,
//                        "Aman",
//                        "IT",
//                        "Senior Developer",
//                        120000,
//                        29,
//                        true,
//                        "Delhi",
//                        List.of("Java", "Spring", "Kafka"),
//                        List.of(
//                                new Project2("Phoenix", "Google", 12, 200000),
//                                new Project2("Atlas", "Amazon", 8, 150000)
//                        )
//                ),
//
//                new Employee2(
//                        2,
//                        "Neha",
//                        "HR",
//                        "HR Manager",
//                        90000,
//                        35,
//                        true,
//                        "Bangalore",
//                        List.of("Hiring", "Communication"),
//                        List.of(
//                                new Project2("HiringDrive", "Internal", 6, 50000)
//                        )
//                ),
//
//                new Employee2(
//                        3,
//                        "Rohit",
//                        "IT",
//                        "Architect",
//                        180000,
//                        40,
//                        false,
//                        "Delhi",
//                        List.of("Java", "Microservices", "AWS"),
//                        List.of(
//                                new Project2("Phoenix", "Google", 12, 200000),
//                                new Project2("Nimbus", "Microsoft", 15, 300000)
//                        )
//                ),
//
//                new Employee2(
//                        4,
//                        "Simran",
//                        "Finance",
//                        "Analyst",
//                        95000,
//                        31,
//                        true,
//                        "Mumbai",
//                        List.of("Excel", "Accounting"),
//                        List.of(
//                                new Project2("FinEdge", "Goldman", 10, 120000)
//                        )
//                ),
//
//                new Employee2(
//                        5,
//                        "Kunal",
//                        "IT",
//                        "Developer",
//                        85000,
//                        26,
//                        true,
//                        "Bangalore",
//                        List.of("Java", "React"),
//                        List.of(
//                                new Project2("Atlas", "Amazon", 8, 150000)
//                        )
//                ),
//
//                new Employee2(
//                        6,
//                        "Priya",
//                        "IT",
//                        "Developer",
//                        85000,
//                        26,
//                        true,
//                        "Pune",
//                        List.of("Java", "React", "Java Script"),
//                        List.of(new Project2("Galaxy", "Zscalar", 13, 450000))
//                ),
//                new Employee2(
//                        7,
//                        "Prabh",
//                        "HR",
//                        "Hiring HR",
//                        45000,
//                        23,
//                        true,
//                        "Mumbai",
//                        List.of("Java", "React,Excel"),
//                        List.of(
//                                new Project2("Nimbus", "Watermark", 10, 700000)
//                        )
//                ),
//                new Employee2(
//                        8,
//                        "Harsh",
//                        "Finance",
//                        "Accountant",
//                        95000,
//                        26,
//                        true,
//                        "Pune",
//                        List.of("C++", "Python"),
//                        List.of(
//                                new Project2("Goblit", "Optum", 8, 500000)
//                        )
//                )
//        );
//        /// //////////////////////////////////////////
////        Problem StatementExtract a unique, alphabetized (A-Z) list of all skills possessed by active Employee2s.
////        The solution must filter out inactive Employee2s,
////        extract the nested skills list,
////        remove duplicates, and sort the final result.
////
////        Expected Outputjava["Communication", "Excel", "Hiring", "Java", "Kafka", "React", "Spring"]
//
////        List<String> activeSkills = Employee2s.stream()
////                .filter(Employee2::isActive)
////                .flatMap(Employee2 -> Employee2.getSkills().stream())
////                .distinct()
////                .sorted()
////                .collect(Collectors.toUnmodifiableList());
//
////        List<String> activeSkills1 = Employee2s.stream()
////                .filter(Employee2::isActive)
////                // 1. Defend against NullPointerException if getSkills() returns null
////                .filter(Employee2 -> Employee2.getSkills() != null)
////                // 2. Safely open the stream since we verified it exists
////                .flatMap(Employee2 -> Employee2.getSkills().stream())
////                .distinct()
////                .sorted()
////                .collect(Collectors.toUnmodifiableList());
//        /// //////////////////////////////////////////
//
////        Problem Statement:
////        Extract a single, comma-separated string containing the names of all Employee2s who meet both of the following criteria:
////        They earn a salary greater than 100,000.
////        They possess "Java" as one of their skills.
////        The final string must display the names in the order they appear in the source list,
////        separated strictly by a comma and a single space (e.g., Name1, Name2).
////
////       Expected Output: "Aman, Rohit"
//
////        String andfd = Employee2s.stream()
////                .filter(Employee2 -> Employee2.getSalary() > 100000)
////                .filter(Employee2 -> Employee2.getSkills().contains("Java"))
////                .map(Employee2::getName)
////                .collect(Collectors.joining(", "));
//
//        /// /////////////////////////////////////////
////        Problem StatementCalculate the total financial budget allocated to all Project2s,
////        grouped by the Employee2's department.
////        Your stream pipeline must group Employee2s by department,
////        traverse down into each Employee2's nested Project2 list,
////        extract the budgets,
////        and sum them up cleanly per department.
////
////        Expected Output{
////            "IT"=1000000.0,
////            "HR"=50000.0,
////            "Finance"=120000.0
////        }
//
////        Map<String, Double> departmentBudgetMap = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.flatMapping(
////                                Employee2 -> Employee2.getProject2s().stream(),
////                                Collectors.summarizingDouble(Project2::getBudget)
////                        )
////                ));
//        /// //////////////////////////////////////////
//
//        //Problem StatementFind the Employee2 with the highest salary in each location.
//        // Your stream pipeline must group Employee2s by their location field,
//        // look through the salaries of the Employee2s in that location group,
//        // and return only the top earner for each city.
//        //
//        // Expected Output:
////        {
////            "Delhi"=Rohit,
////             "Bangalore"=Neha,
////             "Mumbai"=Simran,
////             "Pune"=Priya
////        }
//
////        Map<String, String> highestEarnerNamesByLocation = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getLocation,
////                        Collectors.collectingAndThen(
////                                Collectors.maxBy(Comparator.comparingDouble(Employee2::getSalary)),
////                                optionalEmp -> optionalEmp.map(Employee2::getName).orElse("N/A")
////                        )
////                ));
//
//
//        ////////////////////////////////////////
////        Problem StatementIdentify the Employee2 who is managing the highest number of Project2s.
////        If there is a tie (multiple Employee2s have the exact same Project2 count),
////        break the tie by choosing the Employee2 with the higher salary.
////        Return the result as an Optional<Employee2> to handle empty data gracefully.
////
////        Expected Output:
////        Optional[Employee2(id=1, name=Aman, department=IT, ...)]
//
//
//        Optional<Employee2> topWorker = Employee2s.stream()
//                .max(
//                        // 1. Compare by number of Project2s (handling null Project2 lists gracefully)
//                        Comparator.comparingInt((Employee2 emp) -> emp.getProject2s() == null ? 0 : emp.getProject2s().size())
//                                // 2. Tie-breaker: Compare by salary if Project2 counts match
//                                .thenComparingDouble(Employee2::getSalary)
//                );
//
//
//        int x = 3;
//        /// ////////////////////////////////////
//
////        Requirement
////        We want:
////        department -> Employee2 names
////
////        Expected:
////        {
////            IT=[Aman, Rohit, Kunal, Priya],
////            HR=[Neha],
////            Finance=[Simran]
////        }
//
//
////        Map<String, List<String>> out1ddf = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.mapping(
////                                Employee2::getName,
////                                Collectors.toList()
////                        )
////                ));
////        Now Important Insight:
////        Inside grouping collector, we need:    transform while collecting, NOT after.
////
////        That’s EXACTLY why:
////        mapping() exists.
//
//        ///////////////////////////////////
//
////        Question
////        Get:
////        department -> UNIQUE Employee2 locations
////
////        Expected:
////        {
////            IT=[Delhi, Bangalore],
////            HR=[Mumbai],
////            Finance=[Pune]
////        }
//
////        Map<String, Set<String>> outdsldf = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.mapping(
////                                Employee2::getLocation,
////                                Collectors.toSet()
////                        )
////                ));
//        /// ///////////////////////////////
//
////        Question 4 — filtering()
////        Requirement
////        Get:
////        department -> ACTIVE Employee2 names
////
////        Expected:
////        {
////            IT=[Aman, Kunal],
////            HR=[Neha],
////            Finance=[Simran]
////        }
//
////        Map<String, List<String>> outsdjsdbf = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.filtering(
////                                Employee2::isActive,
////                                Collectors.mapping(
////                                        Employee2::getName,
////                                        Collectors.toList()
////                                )
////                        )
////                ));
//        /// ////////////////////////////
////        Question
////        Get:
////        department -> count of ACTIVE Employee2s
////
////        Expected:
////        {
////            IT=2,
////            HR=1,
////            Finance=1
////        }
////        Map<String, Long> outdfdsf = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.filtering(
////                                Employee2::isActive,
////                                Collectors.counting()
////                        )
////                ));
//        /// //////////////////////////////
//
////        Question 6 — flatMapping()
////        Requirement
////        Get:
////        department -> all Employee2 skills
////
////        Expected:
////        {
////            IT=[Java, Spring, Kafka, AWS, React],
////            HR=[Hiring, Communication],
////            Finance=[Excel, Accounting]
////        }
////        Map<String, Set<String>> outfdsds = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.flatMapping(
////                                Employee2 -> Employee2.getSkills().stream(),
////                                Collectors.toSet()
////                        )
////                ));
//        /// /////////////////////////////
////        Question 7 — summarizingDouble()
////        Requirement
////        Get:
////        department -> salary statistics
////
////        Each department should contain:
////        count
////        sum
////        average
////        min
////        max
////
////        Expected Output Shape
////        {
////            IT=DoubleSummaryStatistics{
////                count=4,
////                sum=395000,
////                min=75000,
////                average=98750,
////                max=120000
////        },
////
////            HR=...
////        }
//
//
////        Map<String, DoubleSummaryStatistics> outdnjkffd = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.summarizingDouble(Employee2::getSalary)
////                ));
//
//
//        /// /////////////////////////////
////        Question
////        Get:
////        department -> average salary only
////
////        Expected:
////        {
////            IT=98750.0,
////            HR=70000.0,
////            Finance=95000.0
////        }
//
////        Map<String, Double> outfdsfdsf = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.collectingAndThen(
////                                Collectors.summarizingDouble(Employee2::getSalary),
////                                DoubleSummaryStatistics::getAverage
////                        )
////                ));
//        /// /////////////////////////////
////        Question 9 — teeing()
////        Requirement
////        For each department, get:
////
////        Employee2 count + average salary
//
//        // Create DTO
//        record DeptStats(
//                long count,
//                double avgSalary
//        ) {
//        }
//
////        Expected Output
////        {
////            IT=DeptStats[count=4, avgSalary=98750],
////            HR=DeptStats[count=2, avgSalary=70000]
////        }
////        Map<String, DeptStats> outdsndf = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.teeing(
////                                Collectors.counting(),
////                                Collectors.averagingDouble(Employee2::getSalary),
////                                (count, avg) -> new DeptStats(count, avg)
////                        )
////                ));
//
//
//        ////////////////////////////////
////        Question 10 — partitioningBy()
////        Requirement
////        Partition Employee2s into:
////        active
////        inactive
////
////        Expected Output
////        {
////            true=[Aman, Neha, Simran, Kunal],
////            false=[Rohit, Priya]
////        }
////        Map<Boolean, List<String>> outdasas = Employee2s.stream()
////                .collect(Collectors.partitioningBy(
////                        Employee2::isActive,
////                        Collectors.mapping(
////                                Employee2::getName,
////                                Collectors.toList()
////                        )
////                ));
//        /// /////////////////////////////
////        Question 11
////        Get:
////        count of active vs inactive Employee2s
////
////        Expected:
////        {
////            true=4,
////            false=2
////        }
//
////        Map<Boolean, Long> outjknsdf = Employee2s.stream()
////                .collect(Collectors.partitioningBy(
////                        Employee2::isActive,
////                        Collectors.counting()
////                ));
//        ////////////////////////////////
//      /*  Requirement
//        Get:
//        department -> total salary of ACTIVE Employee2s
//
//        Expected:
//        {
//            IT=290000,
//            HR=90000,
//            Finance=95000
//        }
//        */
//
////        Map<String, Double> out = Employee2s.stream()
////                .filter(Employee2 -> Employee2.isActive())
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.summingDouble(Employee2::getSalary)
////                ));
//
////        Map<String, Double> out1 = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.filtering(
////                                Employee2::isActive,
////                                Collectors.summingDouble(Employee2::getSalary)
////                        )
////                ));
////
////        out.forEach((k, v) -> System.out.println(k + ":" + v));
///// /////////////////////////////////////////////////////////////////
//      /*  Requirement
//
//        Get:
//
//        department -> highest salary Employee2 NAME
//        Expected Output
//        {
//            IT=Rohit,
//            HR=Neha,
//            Finance=Simran
//        }
//
//       */
//
////        Map<String, String> out2 = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.collectingAndThen(
////                                Collectors.maxBy(Comparator.comparing(Employee2::getSalary)),
////                                opt -> opt.map(Employee2::getName)
////                                        .orElse(null)
////                        )
////                ));
//
//        ///////////////////////////////////////////////////////////////////////////////
//   /*     Requirement
//        Get:
//        department -> highest paid ACTIVE Employee2 name
//
//        Expected:
//        {
//            IT=Aman,
//            HR=Neha,
//            Finance=Simran
//        }
//    */
//
////        Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.collectingAndThen(
////                                Collectors.filtering(
////                                        Employee2::isActive,
////                                        Collectors.maxBy(Comparator.comparing(Employee2::getSalary))
////                                ),
////                                opt -> opt.map(Employee2::getName)
////                                        .orElse(null)
////                        )
////                ));
////        Map<String, String> out3 = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.collectingAndThen(
////
////                                Collectors.filtering(
////                                        Employee2::isActive,
////                                        Collectors.maxBy(Comparator.comparing(Employee2::getSalary))
////                                ),
////
////                                opt -> opt.map(Employee2::getName)
////                                        .orElse(null)
////                        )
////                ));
//        /// ////////////////////////////////////////////////////
//   /* Requirement
//    Get:
//    department -> unique skills of Employee2s
//
//    Expected Output:
//    {
//        IT=[Java, Spring, Kafka, Microservices, AWS, React],
//        HR=[Hiring, Communication],
//        Finance=[Excel, Accounting]
//    }
//    */
//
////        Map<String, Set<String>> out4 = Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.flatMapping(
////                                Employee2 -> Employee2.getSkills().stream(),
////                                Collectors.toSet()
////                        )
////                ));
///// /////////////////////////////////////////////////////////////
//
//      /*  Requirement
//        Get:
//        department -> SORTED Employee2 names
//
//        Expected:
//        {
//            IT=[Aman, Kunal, Priya, Rohit],
//            HR=[Neha],
//            Finance=[Simran]
//        }
//       */
////        Employee2s.stream()
////                .collect(Collectors.groupingBy(
////                        Employee2::getDepartment,
////                        Collectors.collectingAndThen(
////                                Collectors.mapping(Employee2::getName, Collectors.toList()),
////                                list -> {
////                                    list.sort(String::compareTo);
////                                    return list;
////                                }
////                        )
////                ));
//
//
//    }
//
//
//}
