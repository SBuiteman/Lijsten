/**
 * Stijn Buiteman
 * <Lists>
 */
package com.example.stijn.lists;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class contains an onClick method for the button: addToDoButton to add items to a to-do
 * ArrayList toDos, and an onLongClick method to remove items from toDos. The ArrayList is saved
 * in a textfile after each addToDoButton click and this textfile is read at each onCreate call.
 * The toDos are passed to AdapterActivity. List items are clickable and upon click the
 * EditListActivity is started. The name of the item clicked is passed along and a new list is
 * made bearing the name of the clicked item. The onStart method is called to restore the app
 * to its state before it was closed based on information stored in shared preferences.
 */
public class MainActivity extends AppCompatActivity {

    // object belonging to class
    public Button addToDoButton;
    public ArrayList<String> toDos;
    public ListView toDoListView;
    public AdapterActivity myAdapter;
    public EditText userInput;

    /**
     * onCreate reads the textfile containing the old to-do entries and passes them to the
     * adapter. Three click listeners are instantiated to handle an add and remove item event
     * and to select an item and go to its list in the EditListActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDos = new ArrayList<>();
        addToDoButton = (Button) findViewById(R.id.addToDoButton);
        userInput = (EditText) findViewById(R.id.listInputET);
        toDoListView = (ListView) findViewById(R.id.toToListView);

        //reading text from file listSafe.text to restore toDos
        try {
            // read the file and add a to-do for each newline.
            Scanner scan = new Scanner(openFileInput("listSafe.txt"));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                toDos.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    writeTextFile("list", toDos);

                    // tell user nothing to add
                } else {
                    Toast.makeText(MainActivity.this, R.string.emptyText,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * On ListView item click a toast message is displayed and EditListActivity is started.
         * The item selected is send with extras to open the right textfile in EditListActivity.
         */
        toDoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String toDo = toDos.get(position);

                String text = getString(R.string.clickedOn) + toDo;
                Toast toast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
                toast.show();

                Intent myIntent = new Intent(MainActivity.this, EditListActivity.class);
                myIntent.putExtra("key", toDo);
                startActivity(myIntent);
            }
        });

        /**
         * onItemLongClick listener that handles removing to-dos via long-clicking them. The
         * adapter is notified of the change. The textfile containing the sublist is also deleted.
         */
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                final String toDo = toDos.get(position);
                String filename = toDo + "Safe.txt";
                deleteFile(filename);

                // remove the item from the array
                toDos.remove(position);

                // call writeTextFile to remove item from file
                writeTextFile("list", toDos);

                // call a UI update for the list
                myAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, R.string.deletedItem, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        // make AdapterActivity object of this class to enable calls to it,link with ListView
        myAdapter = new AdapterActivity(this, toDos);
        toDoListView.setAdapter(myAdapter);

    }

    /**
     * rewrites the listSafe.txt file with toDos from which an item was removed to store it's
     * removal
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
     * On Starting, the contents of SharedPreferences are checked to see which activity was last
     * open and which list was being viewed. If needed the EditListActivity is started. Old
     * userinput it put back in the userInput EditText.
     */
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        boolean stoppedEditListActivity = prefs.getBoolean("ELAorMA", false);
        String listName = prefs.getString("listname", null);

        // if stoppedEditListActivity is true start EditListActivity
        if (stoppedEditListActivity == true) {
            Intent myIntent = new Intent(MainActivity.this, EditListActivity.class);
            myIntent.putExtra("key", listName);
            startActivity(myIntent);
        }
        else {
            String oldInput = prefs.getString("userinput", null);
            userInput.setText(oldInput);
        }

        // clear the contents of sharedpreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * When the app is stopped  any content in the userInput Edittext is stored in
     * SharedPreferences.
     */
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("Myprefs", MODE_PRIVATE).edit();
        editor.putString("userinput", userInput.getText().toString());
        editor.commit();
    }
}