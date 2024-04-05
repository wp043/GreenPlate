package com.example.greenplate.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.greenplate.R;
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Meal;
import com.example.greenplate.viewmodels.InputMealViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputMealFragment extends Fragment {

    private InputMealViewModel inputMealVM;
    private EditText caloriesEditText;
    private EditText nameEditText;
    private Button submitButton;
    private Button caloriesLeftButton;

    public InputMealFragment() {
        inputMealVM = new InputMealViewModel();
    }

    // Rename and change types and number of parameters
    public static InputMealFragment newInstance(String param1, String param2) {
        InputMealFragment fragment = new InputMealFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_meal, container, false);
        nameEditText = view.findViewById(R.id.im_mealname_input);
        caloriesEditText = view.findViewById(R.id.im_calorie_input);
        submitButton = view.findViewById(R.id.im_submit);
        caloriesLeftButton = view.findViewById(R.id.calorie_goal_graph_button);

        TextView date = (TextView) view.findViewById(R.id.im_date);
        TextView height = (TextView) view.findViewById(R.id.im_height_display);
        TextView weight = (TextView) view.findViewById(R.id.im_weight_display);
        TextView age = (TextView) view.findViewById(R.id.im_age_display);
        TextView gender = (TextView) view.findViewById(R.id.im_gender_display);
        TextView goal = (TextView) view.findViewById(R.id.im_goal_display);
        TextView intake = (TextView) view.findViewById(R.id.im_daily_intake);

        String dateText = "<b>Today's Date: </b>" + inputMealVM.getDateToday();
        date.setText(Html.fromHtml(dateText));

        inputMealVM.getUserHeight(height);
        inputMealVM.getUserWeight(weight);
        inputMealVM.getUserAge(age);
        inputMealVM.getUserGender(gender);
        inputMealVM.getCalorieGoal(goal);
        inputMealVM.getIntakeToday(intake);

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String calories = caloriesEditText.getText().toString();
            if (inputMealVM.isInputDataValid(name, calories, nameEditText, caloriesEditText)) {
                Meal currMeal = new Meal(nameEditText.getText().toString(),
                        Long.parseLong(calories));
                GreenPlateStatus status = inputMealVM.addMealToDatabase(currMeal);
                nameEditText.getText().clear();
                caloriesEditText.getText().clear();
                hideKeyboardFrom(getContext(), view);
                Toast.makeText(getContext(),
                                "Meal added successfully.",
                                Toast.LENGTH_SHORT)
                        .show();
                Log.d("Info", status.toString());
            }
        });


        caloriesLeftButton.setOnClickListener(v -> {
            String goalText = goal.getText().toString();
            String intakeText = intake.getText().toString();

            int caloriesGoal = 0;
            long caloriesIntake = 0;

            try {
                caloriesGoal = Integer.parseInt(goalText.replaceAll("[^0-9]", ""));
                caloriesIntake = Long.parseLong(intakeText.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getContext(), CaloriesLeftActivity.class);
            intent.putExtra("caloriesIntake", caloriesIntake);
            intent.putExtra("caloriesGoal", caloriesGoal);
            startActivity(intent);

        });

        // Button Listeners
        Button mealBreakdownButton = view.findViewById(R.id.meal_breakdown_graph_button);

        mealBreakdownButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MealBreakdownChartActivity.class);
            startActivity(intent);
        });
        return view;
    }

    // Method for closing the keyboard in fragment.
    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
