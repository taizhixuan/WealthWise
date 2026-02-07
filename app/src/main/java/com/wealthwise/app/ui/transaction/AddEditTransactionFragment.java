package com.wealthwise.app.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.model.RecurrenceInterval;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.databinding.FragmentAddEditTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditTransactionFragment extends Fragment {

    private static final long NEW_TRANSACTION_ID = -1L;

    private FragmentAddEditTransactionBinding binding;
    private TransactionViewModel viewModel;

    private long transactionId = NEW_TRANSACTION_ID;
    private boolean isEditing = false;
    private Date selectedDate = new Date();
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    private List<CategoryEntity> categoryList = new ArrayList<>();
    private List<AccountEntity> accountList = new ArrayList<>();
    private CategoryEntity selectedCategory;
    private AccountEntity selectedAccount;
    private RecurrenceInterval selectedInterval = RecurrenceInterval.MONTHLY;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        if (getArguments() != null) {
            transactionId = getArguments().getLong("transactionId", NEW_TRANSACTION_ID);
        }
        isEditing = transactionId != NEW_TRANSACTION_ID;

        setupTypeToggle();
        setupDatePicker();
        setupRecurringOptions();
        setupDropdowns();
        setupSaveButton();

        if (isEditing) {
            loadTransaction();
        }
    }

    private void setupTypeToggle() {
        binding.toggleTransactionType.check(R.id.btn_type_expense);
    }

    private void setupDatePicker() {
        updateDateDisplay();
        binding.etDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedDate = calendar.getTime();
                    updateDateDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateDisplay() {
        binding.etDate.setText(dateFormat.format(selectedDate));
    }

    private void setupRecurringOptions() {
        binding.switchRecurring.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.llRecurringOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        binding.llRecurringOptions.setVisibility(View.GONE);

        String[] intervals = {"Daily", "Weekly", "Monthly", "Quarterly", "Yearly"};
        RecurrenceInterval[] intervalValues = {
                RecurrenceInterval.DAILY, RecurrenceInterval.WEEKLY,
                RecurrenceInterval.MONTHLY, RecurrenceInterval.QUARTERLY,
                RecurrenceInterval.YEARLY
        };

        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                intervals
        );
        binding.actvInterval.setAdapter(intervalAdapter);
        binding.actvInterval.setText("Monthly", false);
        binding.actvInterval.setOnItemClickListener((parent, view, position, id) ->
                selectedInterval = intervalValues[position]);

        binding.etEndDate.setOnClickListener(v -> {
            Calendar endCal = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        endCal.set(year, month, dayOfMonth);
                        binding.etEndDate.setText(dateFormat.format(endCal.getTime()));
                    },
                    endCal.get(Calendar.YEAR),
                    endCal.get(Calendar.MONTH),
                    endCal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void setupDropdowns() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList = categories;
                List<String> categoryNames = new ArrayList<>();
                for (CategoryEntity cat : categories) {
                    categoryNames.add(cat.getName());
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        categoryNames
                );
                binding.actvCategory.setAdapter(categoryAdapter);
                binding.actvCategory.setOnItemClickListener((parent, view, position, id) ->
                        selectedCategory = categoryList.get(position));
            }
        });

        viewModel.getAccounts().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList = accounts;
                List<String> accountNames = new ArrayList<>();
                for (AccountEntity acc : accounts) {
                    accountNames.add(acc.getName());
                }
                ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        accountNames
                );
                binding.actvAccount.setAdapter(accountAdapter);
                binding.actvAccount.setOnItemClickListener((parent, view, position, id) ->
                        selectedAccount = accountList.get(position));
            }
        });
    }

    private void loadTransaction() {
        viewModel.getTransactionById(transactionId).observe(getViewLifecycleOwner(), transactionWithCategory -> {
            if (transactionWithCategory == null) return;

            TransactionEntity transaction = transactionWithCategory.transaction;

            binding.etAmount.setText(String.valueOf(transaction.getAmount()));
            binding.etPayee.setText(transaction.getPayee());
            binding.etNote.setText(transaction.getNote());

            if (transaction.getType() == TransactionType.INCOME) {
                binding.toggleTransactionType.check(R.id.btn_type_income);
            } else {
                binding.toggleTransactionType.check(R.id.btn_type_expense);
            }

            if (transaction.getDate() != null) {
                selectedDate = transaction.getDate();
                calendar.setTime(selectedDate);
                updateDateDisplay();
            }

            if (transactionWithCategory.category != null) {
                selectedCategory = transactionWithCategory.category;
                binding.actvCategory.setText(transactionWithCategory.category.getName(), false);
            }

            if (transaction.getAccountId() > 0 && accountList != null) {
                for (AccountEntity acc : accountList) {
                    if (acc.getId() == transaction.getAccountId()) {
                        selectedAccount = acc;
                        binding.actvAccount.setText(acc.getName(), false);
                        break;
                    }
                }
            }
        });
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveTransaction();
            }
        });
    }

    private boolean validateInput() {
        boolean valid = true;

        String amountStr = binding.etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            binding.tilAmount.setError(getString(R.string.error_amount_required));
            valid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    binding.tilAmount.setError(getString(R.string.error_amount_positive));
                    valid = false;
                } else {
                    binding.tilAmount.setError(null);
                }
            } catch (NumberFormatException e) {
                binding.tilAmount.setError(getString(R.string.error_amount_invalid));
                valid = false;
            }
        }

        if (selectedCategory == null) {
            binding.tilCategory.setError(getString(R.string.error_category_required));
            valid = false;
        } else {
            binding.tilCategory.setError(null);
        }

        if (selectedAccount == null) {
            binding.tilAccount.setError(getString(R.string.error_account_required));
            valid = false;
        } else {
            binding.tilAccount.setError(null);
        }

        return valid;
    }

    private void saveTransaction() {
        double amount = Double.parseDouble(binding.etAmount.getText().toString().trim());
        String payee = binding.etPayee.getText() != null ? binding.etPayee.getText().toString().trim() : "";
        String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "";

        int checkedId = binding.toggleTransactionType.getCheckedButtonId();
        TransactionType type = (checkedId == R.id.btn_type_income)
                ? TransactionType.INCOME : TransactionType.EXPENSE;

        TransactionEntity transaction = new TransactionEntity();
        if (isEditing) {
            transaction.setId(transactionId);
        }

        transaction.setAmount(amount);
        transaction.setPayee(payee);
        transaction.setNote(note);
        transaction.setType(type);
        transaction.setDate(selectedDate);
        transaction.setCategoryId(selectedCategory.getId());
        transaction.setAccountId(selectedAccount.getId());
        transaction.setCreatedAt(new Date());

        if (isEditing) {
            viewModel.update(transaction);
        } else {
            viewModel.insert(transaction);
        }

        if (binding.switchRecurring.isChecked()) {
            saveRecurringTransaction(transaction, type);
        }

        Snackbar.make(binding.getRoot(),
                isEditing ? R.string.transaction_updated : R.string.transaction_saved,
                Snackbar.LENGTH_SHORT).show();

        Navigation.findNavController(requireView()).navigateUp();
    }

    private void saveRecurringTransaction(TransactionEntity transaction, TransactionType type) {
        RecurringTransactionEntity recurring = new RecurringTransactionEntity();
        recurring.setAmount(transaction.getAmount());
        recurring.setPayee(transaction.getPayee());
        recurring.setNote(transaction.getNote());
        recurring.setType(type);
        recurring.setCategoryId(transaction.getCategoryId());
        recurring.setAccountId(transaction.getAccountId());
        recurring.setStartDate(selectedDate);
        recurring.setActive(true);
        recurring.setInterval(selectedInterval);

        if (binding.etEndDate.getText() != null
                && !TextUtils.isEmpty(binding.etEndDate.getText().toString())) {
            try {
                Date endDate = dateFormat.parse(binding.etEndDate.getText().toString());
                recurring.setEndDate(endDate);
            } catch (Exception e) {
                recurring.setEndDate(null);
            }
        }

        viewModel.insertRecurring(recurring);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
