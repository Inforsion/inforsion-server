package com.inforsion.inforsionserver.domain.report.service;

import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.service.TransactionService;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReportService {
    private final TransactionService transactionService;

    public byte[] generateReportPdf(Integer storeId, LocalDate startDate, LocalDate endDate, PeriodType periodType) throws Exception {

        TransactionConditionDto condition = new TransactionConditionDto();
        condition.setStoreId(storeId);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);

        List<StoreSalesFinancialDto> reportData =
                transactionService.getStoreFinancials(condition, PeriodType.MONTH);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

        InputStream reportStream =
                new ClassPathResource("reports/store_sales_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "매장 매출 리포트");
        parameters.put("STORE_ID", storeId);
        parameters.put("START_DATE", startDate.toString());
        parameters.put("END_DATE", endDate.toString());
        parameters.put("PERIOD_TYPE", periodType.name());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        return outputStream.toByteArray();
    }
}
