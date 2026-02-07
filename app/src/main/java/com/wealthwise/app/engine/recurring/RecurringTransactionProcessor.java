package com.wealthwise.app.engine.recurring;

import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.model.RecurrenceInterval;
import com.wealthwise.app.data.repository.RecurringTransactionRepository;
import com.wealthwise.app.data.repository.TransactionRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecurringTransactionProcessor {

    private final RecurringTransactionRepository recurringRepository;
    private final TransactionRepository transactionRepository;

    public RecurringTransactionProcessor(RecurringTransactionRepository recurringRepository,
                                         TransactionRepository transactionRepository) {
        this.recurringRepository = recurringRepository;
        this.transactionRepository = transactionRepository;
    }

    public int processDueRecurring() {
        Date now = new Date();
        List<RecurringTransactionEntity> dueItems = recurringRepository.getDueRecurring(now);

        if (dueItems == null || dueItems.isEmpty()) {
            return 0;
        }

        int processed = 0;
        for (RecurringTransactionEntity recurring : dueItems) {
            if (!recurring.isActive()) continue;

            // Check if past end date
            if (recurring.getEndDate() != null && now.after(recurring.getEndDate())) {
                continue;
            }

            // Create the transaction
            TransactionEntity transaction = new TransactionEntity();
            transaction.setType(recurring.getType());
            transaction.setAmount(recurring.getAmount());
            transaction.setCategoryId(recurring.getCategoryId());
            transaction.setAccountId(recurring.getAccountId());
            transaction.setDate(recurring.getNextOccurrence());
            transaction.setNote(recurring.getNote());
            transaction.setPayee(recurring.getPayee());

            transactionRepository.insert(transaction);

            // Calculate next occurrence
            Date nextOccurrence = calculateNextOccurrence(
                    recurring.getNextOccurrence(), recurring.getInterval());
            recurringRepository.updateNextOccurrence(recurring.getId(), nextOccurrence);

            processed++;
        }

        return processed;
    }

    public static Date calculateNextOccurrence(Date currentDate, RecurrenceInterval interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        switch (interval) {
            case DAILY:
                cal.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case WEEKLY:
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case BIWEEKLY:
                cal.add(Calendar.WEEK_OF_YEAR, 2);
                break;
            case MONTHLY:
                cal.add(Calendar.MONTH, 1);
                break;
            case QUARTERLY:
                cal.add(Calendar.MONTH, 3);
                break;
            case YEARLY:
                cal.add(Calendar.YEAR, 1);
                break;
        }

        return cal.getTime();
    }
}
