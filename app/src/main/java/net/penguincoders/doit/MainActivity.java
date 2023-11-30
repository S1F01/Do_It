package net.penguincoders.doit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.TasksAdapter;
import net.penguincoders.doit.Model.Task;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private DatabaseHandler db;

    private RecyclerView rvTasks;
    private TasksAdapter tasksAdapter;
    private FloatingActionButton fab;

    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksAdapter = new TasksAdapter(db,MainActivity.this);

        rvTasks = findViewById(R.id.tasksRecyclerView);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setItemAnimator(new DefaultItemAnimator());
        rvTasks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvTasks.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(rvTasks);

        fab = findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskBottomSheet.newInstance().show(getSupportFragmentManager(), NewTaskBottomSheet.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}