package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.penguincoders.doit.Model.Task;
import net.penguincoders.doit.Utils.DatabaseHandler;

public class NewTaskBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText tvTaskText;
    private Button btnSaveTask;

    private DatabaseHandler db;

    public static NewTaskBottomSheet newInstance(){
        return new NewTaskBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        requireDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTaskText = requireView().findViewById(R.id.tv_task_content);
        btnSaveTask = requireView().findViewById(R.id.btn_save_task);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            tvTaskText.setText(task);
            assert task != null;
            if(task.length()>0)
                btnSaveTask.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        tvTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    btnSaveTask.setEnabled(false);
                    btnSaveTask.setTextColor(Color.GRAY);
                }
                else{
                    btnSaveTask.setEnabled(true);
                    btnSaveTask.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        btnSaveTask.setOnClickListener(v -> {
            String text = tvTaskText.getText().toString();
            if(finalIsUpdate){
                db.updateTask(bundle.getInt("id"), text);
            }
            else {
                Task task = new Task();
                task.setContent(text);
                task.setStatus(0);
                db.insertTask(task);
            }
            dismiss();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
