package uz.biosoft.mvvm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NoteViewModel noteViewModel;
    private RecyclerView recyclerView;
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_EDIT = 2;
    private int realId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final NoteAdapter adapter =  new NoteAdapter();
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = findViewById(R.id.button_add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, AddEditActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.
                        getNoteItem(viewHolder.getAdapterPosition()));

            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent =  new Intent(MainActivity.this, AddEditActivity.class);
                realId = note.getId();
                intent.putExtra(AddEditActivity.EXTRA_ID,note.getId());
                intent.putExtra(AddEditActivity.EXTRA_TITLE,note.getTitle());
                intent.putExtra(AddEditActivity.EXTRA_DESCRIPTION,note.getDescription());
                intent.putExtra(AddEditActivity.EXTRA_PRIORITY,note.getPrioraty());
                startActivityForResult(intent,REQUEST_CODE_EDIT);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            String title =  data.getStringExtra(AddEditActivity.EXTRA_TITLE);
            String description =  data.getStringExtra(AddEditActivity.EXTRA_DESCRIPTION);
            int priority =  data.getIntExtra(AddEditActivity.EXTRA_PRIORITY,1);
            noteViewModel.insert(new Note(title,description,priority));
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK){
            int id = getIntent().getIntExtra(AddEditActivity.EXTRA_ID,-1);
            Log.d(TAG, "onActivityResult: id ==  " + id);
            id = realId;
            if (id == -1){
                Toast.makeText(this, "Note can't be update ", Toast.LENGTH_SHORT).show();
                return;
            }
            String title =  data.getStringExtra(AddEditActivity.EXTRA_TITLE);
            String description =  data.getStringExtra(AddEditActivity.EXTRA_DESCRIPTION);
            int priority =  data.getIntExtra(AddEditActivity.EXTRA_PRIORITY,1);
            Note note = new Note(title,description,priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note updated ", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.manin_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteAll:
                noteViewModel.deleteAllNotes();
                return true;
                default: return super.onOptionsItemSelected(item);
        }

    }
}
