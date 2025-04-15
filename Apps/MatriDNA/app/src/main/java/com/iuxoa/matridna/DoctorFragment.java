package com.iuxoa.matridna;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DoctorFragment extends Fragment {

    private TextView tvExpert, tvAppointment;
    private View underlineExpert, underlineAppointment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);

        tvExpert = view.findViewById(R.id.tv_expert);
        tvAppointment = view.findViewById(R.id.tv_appointment);
        underlineExpert = view.findViewById(R.id.underline_expert);
        underlineAppointment = view.findViewById(R.id.underline_appointment);

        // Load AppointmentFragment by default instead of ExpertsFragment
        loadFragment(new AppointmentFragment());
        updateUnderline(true); // Update underline for appointment

        tvExpert.setOnClickListener(v -> {
            loadFragment(new ExpertsFragment());
            updateUnderline(false);
        });

        tvAppointment.setOnClickListener(v -> {
            loadFragment(new AppointmentFragment());
            updateUnderline(true);
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void updateUnderline(boolean isExpertSelected) {
        underlineExpert.setBackgroundColor(
                isExpertSelected ? getResources().getColor(R.color.button_color) : getResources().getColor(android.R.color.transparent)
        );
        underlineAppointment.setBackgroundColor(
                isExpertSelected ? getResources().getColor(android.R.color.transparent) : getResources().getColor(R.color.button_color)
        );
    }
}
