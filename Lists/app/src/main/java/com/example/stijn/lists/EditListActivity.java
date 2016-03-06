package com.example.stijn.lists;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class contains an onClick method for the button: addToDoButton to add items to a to-do
 * ArrayList list, and an onLongClick method to remove items from list. The ArrayList is saved
 * in a textfile after each addToDoButton click and this textfile is read at each onCreate call.
 * The toDos are passed to AdapterActivity. OnStop is called to store information in the
 * shared preferences, this information is used in MainActivity to determine if next time the app
 * should start in EditListActivity or not and what list was viewed.
 */
public class EditListActivity extends AppCompatActivity {

    // objects belonging to class
    public Button addToDoButton;
    public ArrayList<String> toDoList;
    public ListView toDoListView;
    public AdapterActivity myAdapter;
    public EditText userInput;
    public String list;
    public TextView title;
    public MainActivity mainActivity;

    /**
     * onCreate reads the textfile, based on the received String list, containing the old to-do
     * entries and passes them to the adapter. Two click listeners are instantiated to handle an
     * add and remove item event.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        toDoList = new ArrayList<>();
        addToDoButton = (Button) findViewById(R.id.addToDoButton);
        userInput = (EditText) findViewById(R.id.InputET);
        toDoListView = (ListView) findViewById(R.id.toToListView);
        title = (TextView) findViewById(R.id.titleTV);
        list = getIntent().getStringExtra("key");
        mainActivity = new MainActivity();

        // title of current list
        title.setText(list);

        //reading text from file String list + Safe.text to restore toDos
        try {
            // read the file and add a to-do for each newline.
            Scanner scan = new Scanner(openFileInput(list + "Safe.txt"));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                toDoList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // make AdapterActivity object of this class to enable calls to it,link with ListView
        myAdapter = new AdapterActivity(this, toDoList);
        toDoListView.setAdapter(myAdapter);

        /**
         * OnClickListener for addToDoButton, passing new to-do to the adapter and adding the
         * new entry to listSafe.txt.
         */
        addToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = userInput.getText().toString();

                //user must input something
                if (input != null && !input.isEmpty()) {
                    myAdapter.add(userInput.getText().toString());
                    userInput.setText("");

                    // call writeTextFile to add item to file
                    writeTextFile(list, toDoList);

                    // tell user nothing to add
                } else {
                    Toast.makeText(EditListActivity.this, R.string.emptyText,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * onItemLongClick listener that handles removing to-dos via long-clicking them. The
         * adapter is notified of the change.
         */
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // remove the item from the array
                toDoList.remove(position);

                // call writeTextFile to remove item from file
                writeTextFile(list, toDoList);

                // call a UI update for the list
                myAdapter.notifyDataSetChanged();

                Toast.makeText(EditListActivity.this, R.string.deletedItem, Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    /**
     * Rewrites a textfile with toDos from which an item was removed to store it's
     * removal. The textfilename is based on the String file + Safe.txt.
     */
    public void writeTextFile(String file, ArrayList <String> listSize){
        // write input to listSafe.txt, each item on a new line
        String teststr = file + "Safe.txt";
        try {
            OutputStream outputstr = openFileOutput(teststr, MODE_PRIVATE);
            PrintStream out = new PrintStream(outputstr);
            int i = 0;
            while (i < listSize.size()) {
                out.println(listSize.get(i));
                i++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * On start restore user input that had not yet been submitted to the userInput EditText.
     */
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        String oldInput = prefs.getString("userinput", null);
        userInput.setText(oldInput);

        // clear the contents of sharedpreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * When the app is stopped the name of current list, any content in the userInput Edittext and
     * if the app is in the EditListActivity is stored in SharedPreferences.
     */
    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getSharedPreferences("Myprefs", MODE_PRIVATE).edit();
        editor.putString("listname", list);
        editor.putString("userinput", userInput.getText().toString());
        editor.putBoolean("ELAorMA", true);
        editor.commit();
    }
}
