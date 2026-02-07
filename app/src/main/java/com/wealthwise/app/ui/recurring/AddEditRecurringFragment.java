package com.wealthwise.app.ui.recurring;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.model.RecurrenceInterval;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.databinding.FragmentAddEditRecurringBinding;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditRecurringFragment extends Fragment {
    private FragmentAddEditRecurringBinding binding;
    private RecurringViewModel viewModel;
    private long recurringId = -1;
    private RecurringTransactionEntity existingRecurring;

    private long selectedCategoryId = -1;
    private long selectedAccountId = -1;
    private Date selectedStartDate;
    private Date selectedEndDate;
    private TransactionType selectedType = TransactionType.EXPENSE;
    private RecurrenceInterval selectedInterval = RecurrenceInterval.MONTHLY;

    private List<CategoryEntity> categoryList = new ArrayList<>();
    private List<AccountEntity> accountList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEditRecurringBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecurringViewModel.class);

        if (getArguments() != null) {
            recurringId = getArguments().getLong("recurringId", -1);
        }

        selectedStartDate = new Date();

        setupTypeToggle();
        setupCategoryDropdown();
        setupAccountDropdown();
        setupIntervalDropdown();
        setupDatePickers();
        setupSaveButton();

        if (recurringId != -1) {
            loadExistingRecurring();
        }
    }

    private void setupTypeToggle() {
        binding.toggleType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_expense) {
                    selectedType = TransactionType.EXPENSE;
                } else if (checkedId == R.id.btn_income) {
                    selectedType = TransactionType.INCOME;
                }
            }
        });

        binding.btnExpense.setChecked(true);
    }

    private void setupCategoryDropdown() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList = categories;
                List<String> categoryNames = new ArrayList<>();
                for (CategoryEntity category : categories) {
                    categoryNames.add(category.getName());
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        categoryNames
                );
                binding.actvCategory.setAdapter(categoryAdapter);

                binding.actvCategory.setOnItemClickListener((parent, v, position, id) ->
                        selectedCategoryId = categoryList.get(position).getId());

                if (existingRecurring != null) {
                    Long catId = existingRecurring.getCategoryId();
                    if (catId != null) {
                        for (int i = 0; i < categories.size(); i++) {
                            if (categories.get(i).getId() == catId) {
                                binding.actvCategory.setText(categories.get(i).getName(), false);
                                selectedCategoryId = catId;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void setupAccountDropdown() {
        viewModel.getAccounts().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList = accounts;
                List<String> accountNames = new ArrayList<>();
                for (AccountEntity account : accounts) {
                    accountNames.add(account.getName());
                }

                ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        accountNames
                );
                binding.actvAccount.setAdapter(accountAdapter);

                binding.actvAccount.setOnItemClickListener((parent, v, position, id) ->
                        selectedAccountId = accountList.get(position).getId());

                if (existingRecurring != null) {
                    Long accId = existingRecurring.getAccountId();
                    if (accId != null) {
                        for (int i = 0; i < accounts.size(); i++) {
                            if (accounts.get(i).getId() == accId) {
                                binding.actvAccount.setText(accounts.get(i).getName(), false);
                                selectedAccountId = accId;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void setupIntervalDropdown() {
        String[] intervalLabels = {"Daily", "Weekly", "Biweekly", "Monthly", "Quarterly", "Yearly"};
        RecurrenceInterval[] intervalValues = {
                RecurrenceInterval.DAILY, RecurrenceInterval.WEEKLY,
                RecurrenceInterval.BIWEEKLY, RecurrenceInterval.MONTHLY,
                RecurrenceInterval.QUARTERLY, RecurrenceInterval.YEARLY
        };

        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                intervalLabels
        );
        binding.actvInterval.setAdapter(intervalAdapter);
        binding.actvInterval.setText(intervalLabels[3], false);

        binding.actvInterval.setOnItemClickListener((parent, v, position, id) ->
                selectedInterval = intervalValues[position]);
    }

    private void setupDatePickers() {
        binding.etStartDate.setText(DateUtils.formatDate(selectedStartDate));
        binding.etStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.etEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        if (isStartDate && selectedStartDate != null) {
            calendar.setTime(selectedStartDate);
        } else if (!isStartDate && selectedEndDate != null) {
            calendar.setTime(selectedEndDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    Date date = selected.getTime();

                    if (isStartDate) {
                        selectedStartDate = date;
                        binding.etStartDate.setText(DateUtils.formatDate(date));
                    } else {
                        selectedEndDate = date;
                        binding.etEndDate.setText(DateUtils.formatDate(date));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveRecurring());
    }

    private void loadExistingRecurring() {
        viewModel.getRecurringById(recurringId).observe(getViewLifecycleOwner(), recurring -> {
            if (recurring != null) {
                existingRecurring = recurring;
                populateFields(recurring);
            }
        });
    }

    private void populateFields(RecurringTransactionEntity recurring) {
        binding.etAmount.setText(String.valueOf(recurring.getAmount()));
        binding.etPayee.setText(recurring.getPayee());
        binding.etNote.setText(recurring.getNote());

        if (recurring.getType() == TransactionType.INCOME) {
            binding.btnIncome.setChecked(true);
            selectedType = TransactionType.INCOME;
        } else {
            binding.btnExpense.setChecked(true);
            selectedType = TransactionType.EXPENSE;
        }

        if (recurring.getInterval() != null) {
            selectedInterval = recurring.getInterval();
            String[] intervalLabels = {"Daily", "Weekly", "Biweekly", "Monthly", "Quarterly", "Yearly"};
            RecurrenceInterval[] intervalValues = {
                    RecurrenceInterval.DAILY, RecurrenceInterval.WEEKLY,
                    RecurrenceInterval.BIWEEKLY, RecurrenceInterval.MONTHLY,
                    RecurrenceInterval.QUARTERLY, RecurrenceInterval.YEARLY
            };
            for (int i = 0; i < intervalValues.length; i++) {
                if (intervalValues[i] == recurring.getInterval()) {
                    binding.actvInterval.setText(intervalLabels[i], false);
                    break;
                }
            }
        }

        if (recurring.getStartDate() != null) {
            selectedStartDate = recurring.getStartDate();
            binding.etStartDate.setText(DateUtils.formatDate(recurring.getStartDate()));
        }

        if (recurring.getEndDate() != null) {
            selectedEndDate = recurring.getEndDate();
            binding.etEndDate.setText(DateUtils.formatDate(recurring.getEndDate()));
        }
    }

    private void saveRecurring() {
        String amountStr = binding.etAmount.getText().toString().trim();
        String payee = binding.etPayee.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            binding.tilAmount.setError("Please enter an amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.tilAmount.setError("Invalid amount");
            return;
        }

        if (amount <= 0) {
            binding.tilAmount.setError("Amount must be greater than zero");
            return;
        }

        if (selectedCategoryId == -1) {
            binding.tilCategory.setError("Please select a category");
            return;
        }

        if (selectedAccountId == -1) {
            binding.tilAccount.setError("Please select an account");
            return;
        }

        if (selectedStartDate == null) {
            binding.tilStartDate.setError("Please select a start date");
            return;
        }

        binding.tilAmount.setError(null);
        binding.tilCategory.setError(null);
        binding.tilAccount.setError(null);
        binding.tilStartDate.setError(null);

        RecurringTransactionEntity recurring;
        if (existingRecurring != null) {
            recurring = existingRecurring;
        } else {
            recurring = new RecurringTransactionEntity();
        }

        recurring.setType(selectedType);
        recurring.setAmount(amount);
        recurring.setCategoryId(selectedCategoryId);
        recurring.setAccountId(selectedAccountId);
        recurring.setInterval(selectedInterval);
        recurring.setStartDate(selectedStartDate);
        recurring.setEndDate(selectedEndDate);
        recurring.setPayee(payee);
        recurring.setNote(note);

        if (existingRecurring != null) {
            viewModel.update(recurring);
        } else {
            viewModel.insert(recurring);
        }

        Toast.makeText(requireContext(), "Recurring transaction saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
