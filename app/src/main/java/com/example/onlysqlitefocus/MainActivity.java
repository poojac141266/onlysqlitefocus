package com.example.onlysqlitefocus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Added Toast import
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.onlysqlitefocus.R; // Make sure you import R from your package
import com.example.onlysqlitefocus.ContactDetailsActivity;

public class MainActivity extends AppCompatActivity {
    private static final int CONTACT_DETAIL_REQUEST = 1;

    private ContactDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;

    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure you have a layout file named activity_main

        // Initialize database
        databaseHelper = new ContactDatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        // Initialize RecyclerView and ContactAdapter
        recyclerView = findViewById(R.id.recycler_view);
        contactAdapter = new ContactAdapter(this, getContactsFromDatabase(), databaseHelper); // Pass databaseHelper
        recyclerView.setAdapter(contactAdapter);

        // Initialize EditText fields
        nameEditText = findViewById(R.id.enter_name);
        phoneEditText = findViewById(R.id.enter_phone);
        emailEditText = findViewById(R.id.enter_email);

        // Button for creating a new contact
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertContact();
            }
        });

        // Add an intent to open the detail screen (activity_new_contact) when an item is clicked in the RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Contact selectedContact = contactAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, ContactDetailsActivity.class);
                intent.putExtra("contact_id", selectedContact.getId());
                startActivityForResult(intent, CONTACT_DETAIL_REQUEST);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Handle long item click if needed
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Refresh the RecyclerView to update the contact list
                contactAdapter.updateContacts(getContactsFromDatabase());
            }
        }
    }

    private List<Contact> getContactsFromDatabase() {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM " + ContactDatabaseHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            try {
                int idColumnIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_ID);
                int nameColumnIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME);
                int phoneColumnIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_PHONE);
                int emailColumnIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_EMAIL);
                int photoColumnIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_PHOTO); // Add this line

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumnIndex);
                    String name = cursor.getString(nameColumnIndex);
                    String phone = cursor.getString(phoneColumnIndex);
                    String email = cursor.getString(emailColumnIndex);
                    byte[] photo = cursor.getBlob(photoColumnIndex); // Use the correct index

                    Contact contact = new Contact(id, name, phone, email, photo);
                    contacts.add(contact);
                }
            } finally {
                cursor.close();
            }
        }

        return contacts;
    }
    private void insertContact() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and Phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ContactDatabaseHelper.COLUMN_NAME, name);
        values.put(ContactDatabaseHelper.COLUMN_PHONE, phone);
        values.put(ContactDatabaseHelper.COLUMN_EMAIL, email);

        long newRowId = database.insert(ContactDatabaseHelper.TABLE_NAME, null, values);
        if (newRowId != -1) {
            contactAdapter.updateContacts(getContactsFromDatabase());
            clearInputFields();
        }
    }
    private void clearInputFields() {
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
    }
}