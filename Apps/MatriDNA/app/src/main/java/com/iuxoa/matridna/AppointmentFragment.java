package com.iuxoa.matridna;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import java.util.Calendar;

public class AppointmentFragment extends Fragment {

    private Spinner spinnerDoctor;
    private Button btnSelectDate, btnSelectTime, btnConfirmAppointment;
    private TextView selectedDateText, selectedTimeText;
    private String selectedDoctor;
    private String selectedDate, selectedTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        // Initialize UI elements
        spinnerDoctor = view.findViewById(R.id.spinner_doctor);
        btnSelectDate = view.findViewById(R.id.btn_select_date);
        btnSelectTime = view.findViewById(R.id.btn_select_time);
        btnConfirmAppointment = view.findViewById(R.id.btn_confirm_appointment);

        // Sample doctor list
        String[] doctors = {"Dr. Nehal Heer", "Dr. Shruti Tiwar", "Dr. Priyanka Verma", "Dr. Vaishnavi Rai"};

        // Set up spinner (doctor selection)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, doctors);
        spinnerDoctor.setAdapter(adapter);

        spinnerDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDoctor = doctors[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDoctor = "";
            }
        });

        // Date Picker
        btnSelectDate.setOnClickListener(v -> openDatePicker());

        // Time Picker
        btnSelectTime.setOnClickListener(v -> openTimePicker());

        // Confirm Appointment
        btnConfirmAppointment.setOnClickListener(v -> confirmAppointment());

        return view;
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            btnSelectDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void openTimePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Time");

        // Creating a NumberPicker
        final NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10); // 10 options (10 AM to 8 PM)

        // Defining available time slots
        final String[] timeSlots = {
                "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM",
                "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"
        };

        numberPicker.setDisplayedValues(timeSlots);
        numberPicker.setWrapSelectorWheel(false);

        builder.setView(numberPicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedTime = timeSlots[numberPicker.getValue()];
            btnSelectTime.setText(selectedTime);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }



    private void confirmAppointment() {
        if (selectedDoctor == null || selectedDoctor.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a doctor", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = String.format("✅ Appointment Confirmed!\n\nDoctor: %s\nDate: %s\nTime: %s",
                selectedDoctor, selectedDate, selectedTime);

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

}
