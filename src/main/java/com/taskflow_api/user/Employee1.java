//package com.taskflow_api.user;
//
//
//import com.taskflow_api.TaskflowApiApplication;
//import jakarta.validation.constraints.Null;
//import lombok.*;
//import org.springframework.boot.SpringApplication;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//public class Employee {
//    private int id;
//    private String name;
//    private String department;
//    private double salary;
//    private boolean active;
//
//
//    public static void main(String[] args) {
//
//        List<Employee> employees = List.of(
//                new Employee(1, "Aman", "IT", 90000, true),
//                new Employee(2, "Neha", "HR", 60000, true),
//                new Employee(3, "Rohit", "IT", 120000, false),
//                new Employee(4, "Simran", "Finance", 95000, true),
//                new Employee(5, "Kunal", "IT", 75000, true),
//                new Employee(6, "Priya", "HR", 80000, false)
//        );
///// /////////////////////////////////////////////////
////        Requirement 1
////        Get the names of all ACTIVE employees:
////        salary > 80000
////        sorted by salary descending
////        return as List<String>
//        List<String> output = employees.stream()
//                .filter(Employee::isActive)
//                .filter(emp -> emp.getSalary() > 80000)
//                .sorted(Comparator.comparing(Employee::getSalary).reversed())
//                .map(Employee::getName)
//                .toList();
//
////        for (String x : output) {
////            System.out.println(x);
////        }
//
////        Requirement
////        Find the HIGHEST paid employee from EACH department.
////        Expected Output
////        {
////            IT = Rohit,
////            HR = Neha,
////            Finance = Simran
////        }
//        Map<String, Optional<Employee>> out =
//                employees.stream()
//                        .collect(
//                                Collectors.groupingBy(
//                                        Employee::getDepartment,
//                                        Collectors.maxBy(
//                                                Comparator.comparing(Employee::getSalary)
//                                        )
//                                )
//                        );
//        out.forEach((k, v) -> System.out.println(k + "=" + v.get().getSalary()));
//    }
//}
////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//class Transaction {
//    private String accountNumber;
//    private String type; // CREDIT / DEBIT
//    private double amount;
//    private String region;
//
//    public static void main(String[] args) {
//        List<Transaction> txns = List.of(
//                new Transaction("ACC1", "CREDIT", 1000, "INDIA"),
//                new Transaction("ACC1", "DEBIT", 200, "INDIA"),
//                new Transaction("ACC2", "CREDIT", 3000, "US"),
//                new Transaction("ACC2", "DEBIT", 500, "US"),
//                new Transaction("ACC3", "CREDIT", 1500, "INDIA"),
//                new Transaction("ACC3", "DEBIT", 700, "INDIA")
//        );
//
//        //Get total transaction amount per region.
//        Map<String, Double> out = txns.stream()
//                .collect(Collectors.groupingBy(
//                        Transaction::getRegion,
//                        Collectors.summingDouble(Transaction::getAmount)
//                ));
//
////        for (String key : out.keySet())
////            System.out.println(key + ": " + out.get(key));
////
////      out.forEach((k, v) -> System.out.println(k + "=" + v));
//
//        /// Requirement
//        /// Group transactions by:
//        ///
//        /// region -> type -> total amount
//        //Expected OUTPUT
////    {
////        INDIA = {
////                CREDIT=2500.0,
////                DEBIT=900.0
////        },
////                US = {
////                        CREDIT=3000.0,
////                        DEBIT=500.0
////                }
////    }
//
//        Map<String, Map<String, Double>> out1 =
//                txns.stream()
//                        .collect(Collectors.groupingBy(
//                                Transaction::getRegion,
//                                Collectors.groupingBy(
//                                        Transaction::getType,
//                                        Collectors.summingDouble(Transaction::getAmount)
//                                )
//                        ));
//
//        for (String key : out1.keySet())
//            System.out.println(key + ": " + out1.get(key));
//    }
//
//
//}
////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//
//@Getter
//@Setter
//@AllArgsConstructor
//class Order {
//    private String customer;
//    private String status; // SUCCESS, FAILED, PENDING
//    private double amount;
//
//    public static void main(String[] args) {
//        List<Order> orders = List.of(
//                new Order("Aman", "SUCCESS", 1200),
//                new Order("Neha", "FAILED", 500),
//                new Order("Aman", "SUCCESS", 800),
//                new Order("Rohit", "PENDING", 700),
//                new Order("Neha", "SUCCESS", 1500),
//                new Order("Aman", "FAILED", 200)
//        );
//
////        Requirement
////        Find the TOTAL SUCCESS amount per customer.
////        Ignore FAILED and PENDING orders.
////
////        Expected Output
////        {
////            Aman=2000.0,
////                    Neha=1500.0
////        }
//        Map<String, Double> out =
//                orders.stream()
//                        .filter(order -> "SUCCESS".equals(order.getStatus()))
//                        .collect(Collectors.groupingBy(
//                                Order::getCustomer,
//                                Collectors.summingDouble(Order::getAmount)
//                        ));
//
////        for (String key : out.keySet()) {
////            System.out.println(key + "=" + out.get(key));
////        }
//        out.forEach((k, v) -> System.out.println(k + "=" + v));
//    }
//}
////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//class User1 {
//    private String name;
//    private List<String> roles;
//
//    public static void main(String[] args) {
//        List<User1> users = List.of(
//                new User1("Aman", List.of("ADMIN", "USER")),
//                new User1("Neha", List.of("USER")),
//                new User1("Rohit", List.of("DEV", "ADMIN")),
//                new User1("Simran", List.of("DEV")),
//                new User1("Kunal", List.of())
//        );
//
//        // Requirement
////        Rohit:[DEV, ADMIN]
////        Neha:[USER]
////        Simran:[DEV]
////        Aman:[ADMIN, USER]
////        Kunal:[]
//
//        Map<String, List<String>> out = users.stream()
//                .collect(Collectors.toMap(
//                        User1::getName,
//                        User1::getRoles
//                ));
//
//        out.forEach((k, v) -> System.out.println(k + ":" + v));
//
//        // Requirement
//        // Get all UNIQUE roles across all users.
//
//        // Expected Output:
//        // [ADMIN, USER, DEV]
//
//        List<String> out1 = users.stream()
//                .flatMap(user1 -> user1.getRoles().stream())
//                .distinct()
//                .toList();
//        System.out.println(out1);
//    }
//
//}
////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//class Emp {
//    private String name;
//    private double salary;
//}
//
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//class Department {
//    private String name;
//    private List<Emp> employees;
//
//    public static void main(String[] args) {
//
//        List<Department> departments = List.of(
//
//                new Department(
//                        "IT",
//                        List.of(
//                                new Emp("Aman", 90000),
//                                new Emp("Rohit", 120000)
//                        )
//                ),
//
//                new Department(
//                        "HR",
//                        List.of(
//                                new Emp("Neha", 80000)
//                        )
//                ),
//
//                new Department(
//                        "Finance",
//                        List.of(
//                                new Emp("Simran", 95000)
//                        )
//                )
//        );
//
////        Requirement
////        Get all employee names across all departments.
////
////        Expected:
////        [Aman, Rohit, Neha, Simran]
//        List<String> out = departments.stream()
//                .flatMap(department -> department.getEmployees().stream())
//                .map(Emp::getName)
//                .toList();
//
//        System.out.println(out);
//
////        Requirement
////        Convert:
////        department -> employee names
////
////        Expected:
////        {
////            IT=[Aman, Rohit],
////            HR=[Neha],
////            Finance=[Simran]
////        }
////
////        Return type:
////        Map<String, List<String>>
//
//        Map<String, List<String>> out1 = departments.stream()
//                .collect(Collectors.toMap(
//                        Department::getName,
//                        department -> department.getEmployees()
//                                .stream()
//                                .map(Emp::getName)
//                                .toList()
//                ));
//        out1.forEach((k, v) -> System.out.println(k + ": " + v));
//    }
//}
//
////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//class LogEntry {
//    private String service;
//    private String severity; // INFO, WARN, ERROR
//    private String message;
//
//    public static void main(String[] args) {
//        List<LogEntry> logs = List.of(
//                new LogEntry("AUTH", "ERROR", "Invalid token"),
//                new LogEntry("AUTH", "INFO", "Login success"),
//                new LogEntry("PAYMENT", "ERROR", "Payment failed"),
//                new LogEntry("AUTH", "ERROR", "JWT expired"),
//                new LogEntry("PAYMENT", "WARN", "Slow response"),
//                new LogEntry("PAYMENT", "ERROR", "Gateway timeout")
//        );
//
//
//        Map<String, Long> out = logs.stream()
//                .filter(log -> "ERROR".equals(log.getSeverity()))
//                .collect(Collectors.groupingBy(
//                        LogEntry::getService,
//                        Collectors.counting()
//                ));
//        out.forEach((k, v) -> System.out.println(k + ":" + v));
//        //////////////////////////////////////////////////
//
//        List<String> names = List.of(
//                "Aman",
//                "Neha",
//                "Rohit",
//                "Aman",
//                "Simran",
//                "Neha"
//        );
//
////        Requirement
////        Find duplicate elements from a list.
////        Expected Output:    [Aman, Neha]
//
//        List<String> out1 = names.stream()
//                .collect(Collectors.groupingBy(
//                        name -> name,
//                        Collectors.counting()
//                ))
//                .entrySet()
//                .stream()
//                .filter(entry -> entry.getValue() > 1)
//                .map(Map.Entry::getKey)
//                .toList();
//    }
//}
//
