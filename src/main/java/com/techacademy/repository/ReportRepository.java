package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techacademy.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    // employee_codeが一致する日報を検索してリスト化する
    List<Report> findByEmployeeCode(String employeeCode);

    // report_dateとemployee_codeが一致する日報を検索する
    Optional<Report> findByReportDateAndEmployeeCode(LocalDate reportDate, String employeeCode);

}