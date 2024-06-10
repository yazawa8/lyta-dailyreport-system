package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧全件表示処理
    public List<Report> findAll() {

        return reportRepository.findAll();
    }

    // 日報一覧1名分の全件表示処理
    public List<Report> findByEmployeeCode(String employeeCode){

        return reportRepository.findByEmployeeCode(employeeCode);
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索(reportテーブルのidカラムはint型なのでtoStringでキャスト)
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 日報保存処理
    @Transactional
    public ErrorKinds save(Report report) {

        // 日報重複チェック
        ErrorKinds result = reportDateCheck(report);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }
        // 新規日報の保存に必要なデータをセット
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        // セーブ成功の判定を返す
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新処理
    @Transactional
    public ErrorKinds update(String employeeCode, Integer id, LocalDate reportDate, String title, String content) {

        // 業務チェック
        ErrorKinds result = reportUpdateCheck(id, reportDate, employeeCode);
        if(ErrorKinds.CHECK_OK != result) {
            return result;
        }
        // 更新する日報を呼び出して、データを上書き
        Report report = findById(id);

        report.setReportDate(reportDate);
        report.setTitle(title);
        report.setContent(content);

        report.setUpdatedAt(LocalDateTime.now());

        // セーブ成功の判定を返す
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除処理
    @Transactional
    public void delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

    }

    // ログイン中の従業員かつ入力した日付の日報データが存在するかをチェックする処理
    private ErrorKinds reportDateCheck(Report report) {

        Optional<Report> option = reportRepository.findByReportDateAndEmployeeCode(report.getReportDate(), report.getEmployeeCode());
        Report checkDate = option.orElse(null);

        if(checkDate != null) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        return ErrorKinds.CHECK_OK;
    }

    // 日報を更新する際に同一の日付かつ同じ従業員の日報が存在しないかをチェックする処理
    private ErrorKinds reportUpdateCheck(Integer id, LocalDate reportDate, String employeeCode) {

        Optional<Report> option = reportRepository.findByReportDateAndEmployeeCode(reportDate, employeeCode);
        Report report = option.orElse(null);

        // reportが取得できなかった場合は重複なしの確認を返す
        if(report == null) {
            return ErrorKinds.CHECK_OK;
        }
        // reportを取得した場合、idが同一かチェック
        if(report.getId().equals(id)) {
            return ErrorKinds.CHECK_OK;
        }
        // 別のidの日報と日付が重複しているためエラーを返す
        return ErrorKinds.DATECHECK_ERROR;
    }

}