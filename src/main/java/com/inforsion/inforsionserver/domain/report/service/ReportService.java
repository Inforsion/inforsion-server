package com.inforsion.inforsionserver.domain.report.service;

import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.service.TransactionService;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReportService {

    private final TransactionService transactionService;

    public byte[] generateReportPdf(Integer storeId, LocalDate startDate, LocalDate endDate) throws Exception {
        TransactionConditionDto condition = new TransactionConditionDto();
        condition.setStoreId(storeId);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);

        // 기간별 매출 데이터 조회
        List<StoreSalesFinancialDto> reportData = transactionService.getStoreFinancials(condition);

        InputStream jrxmlInput = new ClassPathResource("reports/financial_summary.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "재무 요약 보고서");
        parameters.put("Period", startDate.toString() + " ~ " + endDate.toString());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

}
