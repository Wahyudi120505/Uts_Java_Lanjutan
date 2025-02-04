package com.example.uts_loan.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPersonalLoanHistoryDto<T> {
    private Integer loanId;
    private Integer amount;
    private Integer remain;
    private String startDate;
    private String dueDate;
    private String tenor;
    private String status;
    private List<T> paymentHistory;  

    public static <T> GetPersonalLoanHistoryDto<T> history(Integer loanId, Integer amount, Integer remain, String startDate, String dueDate, String tenor, String status, List<T> paymentHistory){
        return GetPersonalLoanHistoryDto.<T>builder()
            .loanId(loanId)
            .amount(amount)
            .remain(remain)
            .startDate(startDate)
            .dueDate(dueDate)
            .tenor(tenor)
            .status(status)
            .paymentHistory(paymentHistory)
            .build();
    }
}
