package com.example.onlysqlitefocus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailsActivity extends AppCompatActivity {
    private ContactDatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    private long contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // Initialize database
        databaseHelper = new ContactDatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        // Initialize EditText fields
        nameEditText = findViewById(R.id.enter_name);
        phoneEditText = findViewById(R.id.enter_phone);
        emailEditText = findViewById(R.id.enter_email);

        // Get the contact ID passed from the main activity
        contactId = getIntent().getLongExtra("contact_id", -1);

        // Load the contact details for editing
        loadContactDetails();

        // Button for updating a contact
        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact();
            }
        });

        // Button for deleting a contact
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
            }
        });
    }

    private void loadContactDetails() {
        if (contactId != -1) {
            String query = "SELECT * FROM " + ContactDatabaseHelper.TABLE_NAME +
                    " WHERE " + ContactDatabaseHelper.COLUMN_ID + " = " + contactId;
            Cursor cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_PHONE));
                String email = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_EMAIL));

                nameEditText.setText(name);
                phoneEditText.setText(phone);
                emailEditText.setText(email);

                cursor.close();
            }
        }
    }

    private void updateContact() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        ContentValues values = new ContentValues();
        values.put(ContactDatabaseHelper.COLUMN_NAME, name);
        values.put(ContactDatabaseHelper.COLUMN_PHONE, phone);
        values.put(ContactDatabaseHelper.COLUMN_EMAIL, email);

        int rowsAffected = database.update(ContactDatabaseHelper.TABLE_NAME, values,
                ContactDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(contactId)});

        if (rowsAffected > 0) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void deleteContact() {
        int rowsDeleted = database.delete(ContactDatabaseHelper.TABLE_NAME,
                ContactDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(contactId)});

        if (rowsDeleted > 0) {
            setResult(RESULT_OK);
            finish();
        }
    }
}

