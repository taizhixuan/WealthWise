package com.wealthwise.app.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wealthwise.app.data.local.dao.AccountDao;
import com.wealthwise.app.data.local.dao.BudgetDao;
import com.wealthwise.app.data.local.dao.CategoryDao;
import com.wealthwise.app.data.local.dao.RecurringTransactionDao;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                TransactionEntity.class,
                CategoryEntity.class,
                BudgetEntity.class,
                RecurringTransactionEntity.class,
                AccountEntity.class
        },
        version = 1,
        exportSchema = true
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "wealthwise_db";
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();
    public abstract RecurringTransactionDao recurringTransactionDao();
    public abstract AccountDao accountDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .addCallback(seedDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback seedDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                if (INSTANCE == null) return;

                CategoryDao categoryDao = INSTANCE.categoryDao();
                AccountDao accountDao = INSTANCE.accountDao();

                // Seed default expense categories
                categoryDao.insert(createCategory("Food & Dining", "EXPENSE", "ic_restaurant", "#E91E63", true));
                categoryDao.insert(createCategory("Transportation", "EXPENSE", "ic_directions_car", "#9C27B0", true));
                categoryDao.insert(createCategory("Shopping", "EXPENSE", "ic_shopping_cart", "#673AB7", true));
                categoryDao.insert(createCategory("Bills & Utilities", "EXPENSE", "ic_receipt", "#3F51B5", true));
                categoryDao.insert(createCategory("Entertainment", "EXPENSE", "ic_movie", "#2196F3", true));
                categoryDao.insert(createCategory("Health & Fitness", "EXPENSE", "ic_fitness_center", "#00BCD4", true));
                categoryDao.insert(createCategory("Education", "EXPENSE", "ic_school", "#009688", true));
                categoryDao.insert(createCategory("Personal Care", "EXPENSE", "ic_spa", "#FF9800", true));
                categoryDao.insert(createCategory("Home", "EXPENSE", "ic_home", "#795548", true));
                categoryDao.insert(createCategory("Other Expense", "EXPENSE", "ic_more_horiz", "#607D8B", true));

                // Seed default income categories
                categoryDao.insert(createCategory("Salary", "INCOME", "ic_work", "#2E7D32", true));
                categoryDao.insert(createCategory("Freelance", "INCOME", "ic_laptop", "#43A047", true));
                categoryDao.insert(createCategory("Investments", "INCOME", "ic_trending_up", "#66BB6A", true));
                categoryDao.insert(createCategory("Gifts", "INCOME", "ic_card_giftcard", "#81C784", true));
                categoryDao.insert(createCategory("Other Income", "INCOME", "ic_attach_money", "#A5D6A7", true));

                // Seed default account
                AccountEntity defaultAccount = new AccountEntity();
                defaultAccount.setName("Cash");
                defaultAccount.setAccountType("CASH");
                defaultAccount.setBalance(0);
                defaultAccount.setInitialBalance(0);
                accountDao.insert(defaultAccount);

                AccountEntity bankAccount = new AccountEntity();
                bankAccount.setName("Bank Account");
                bankAccount.setAccountType("BANK");
                bankAccount.setBalance(0);
                bankAccount.setInitialBalance(0);
                accountDao.insert(bankAccount);
            });
        }
    };

    private static CategoryEntity createCategory(String name, String type, String icon, String color, boolean isDefault) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setType(com.wealthwise.app.data.model.TransactionType.valueOf(type));
        category.setIconName(icon);
        category.setColorHex(color);
        category.setDefault(isDefault);
        return category;
    }
}
