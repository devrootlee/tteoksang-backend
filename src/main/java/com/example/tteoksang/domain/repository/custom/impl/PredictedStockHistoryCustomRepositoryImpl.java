package com.example.tteoksang.domain.repository.custom.impl;

import com.example.tteoksang.domain.QKrxStockInfo;
import com.example.tteoksang.domain.QPredictedStockHistory;
import com.example.tteoksang.domain.repository.custom.PredictedStockHistoryCustomRepository;
import com.example.tteoksang.dto.querydto.Top10PredictionStockDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PredictedStockHistoryCustomRepositoryImpl implements PredictedStockHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Top10PredictionStockDto> findTop10(int startDateKey, int endDateKey) {
        QPredictedStockHistory history = QPredictedStockHistory.predictedStockHistory;
        QKrxStockInfo stock = QKrxStockInfo.krxStockInfo;

        return queryFactory
                .select(com.querydsl.core.types.Projections.constructor(
                        Top10PredictionStockDto.class,
                        history.stockId,
                        stock.stockName,
                        stock.market,
                        history.stockId.count()
                ))
                .from(history)
                .join(stock).on(history.stockId.eq(stock.stockId))
                .where(history.dateKey.between(startDateKey, endDateKey))
                .groupBy(history.stockId, stock.stockName, stock.market)
                .orderBy(history.stockId.count().desc(),history.stockId.desc())
                .limit(10)
                .fetch();
    }
}
