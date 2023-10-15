package com.example.onlysqlitefocus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactList;
    private Context context;
    private ContactDatabaseHelper databaseHelper; // Added databaseHelper

    public ContactAdapter(Context context, List<Contact> contactList, ContactDatabaseHelper databaseHelper) {
        this.context = context;
        this.contactList = contactList;
        this.databaseHelper = databaseHelper; // Initialize the database helper
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ContactViewHolder(view);
    }

    public void updateContacts(List<Contact> contacts) {
        contactList.clear();
        contactList.addAll(contacts);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // Implement the getItem method to get a contact
    public Contact getItem(int position) {
        return contactList.get(position);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView phoneTextView;

        ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.enter_name);
            phoneTextView = itemView.findViewById(R.id.enter_phone);
        }

        void bind(Contact contact) {
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());
        }
    }
}