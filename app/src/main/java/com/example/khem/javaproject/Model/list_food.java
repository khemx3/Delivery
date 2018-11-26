package com.example.khem.javaproject.Model;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.khem.javaproject.R;
import com.example.khem.javaproject.add_food;
import com.example.khem.javaproject.main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class list_food extends AppCompatActivity {

    private Button btn_add, btn_delete, btn_home;
    private ListView list_f;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayList<String> listKeys = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private DatabaseReference mDatabase;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food);


        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_home = (Button) findViewById(R.id.btn_home);

        btn_delete.setEnabled(false);
        list_f = (ListView) findViewById(R.id.list_f);

        mDatabase = FirebaseDatabase.getInstance().getReference("menu");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listItems);

        list_f.setAdapter(adapter);
        list_f.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        list_f.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        selectedPosition = position;
                        itemSelected = true;
                        btn_delete.setEnabled(true);
                    }
                });

        addChildEventListener();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(list_food.this, add_food.class));
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(v);
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(list_food.this, main.class));
            }
        });

    }
    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add(
                        (String) dataSnapshot.child("name").getValue());

                listKeys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                if (index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addChildEventListener(childListener);


        ValueEventListener queryValueListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                adapter.clear();
                listKeys.clear();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    String match = (String) next.child("name").getValue();
                    String key = next.getKey();
                    listKeys.add(key);
                    adapter.add(match);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    public void deleteItem(View view) {

        list_f.setItemChecked(selectedPosition, false);
        mDatabase.child(listKeys.get(selectedPosition)).removeValue();
        Toast.makeText(this, "Delete Food", Toast.LENGTH_LONG).show();

    }

}
