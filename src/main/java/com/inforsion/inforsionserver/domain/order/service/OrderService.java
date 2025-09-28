package com.inforsion.inforsionserver.domain.order.service;

import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.domain.order.repository.OrderRepository;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.domain.transaction.repository.TransactionRepository;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    private final int SALES_DAYS = 30;

    @Transactional
    public List<StoreSalesFinancialDto> cancelOrderAndRecalculate(Integer orderId){
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(()->new IllegalArgumentException("주문을 찾을 수 없습니다. + orderId"));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<TransactionEntity> transactions = transactionRepository.findAllByOrdersId(orderId);
        transactionRepository.deleteAll(transactions);

        TransactionConditionDto condition = new TransactionConditionDto();
        condition.setStoreId(order.getTransaction().getStore().getId());
        condition.setStartDate(LocalDate.now().minusDays(SALES_DAYS));
        condition.setEndDate(LocalDate.now());

        return transactionRepository.getStoreFinancials(condition, PeriodType.DAY);
    }

}
