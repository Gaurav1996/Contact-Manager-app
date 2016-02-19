package gauravagrawal.com.contactmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int EDIT=0,DELETE=1;

    EditText nametxt,phonetxt,emailtxt,addresstxt;
    List<Contact> Contacts=new ArrayList<Contact>();
    ListView contactListview;
    ImageView contactImage;
    Uri imageuri=Uri.parse("android.resource://gauravagrawal.com.contactmanager/drawable/no_user.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contact> contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nametxt=(EditText)findViewById(R.id.Nametxt);
        phonetxt=(EditText)findViewById(R.id.Phonetxt);
        emailtxt=(EditText)findViewById(R.id.emailtxt);
        addresstxt=(EditText)findViewById(R.id.addrtxt);
        contactListview=(ListView)findViewById(R.id.listView);
        contactImage=(ImageView)findViewById(R.id.imageViewContactImage);
        dbHandler=new DatabaseHandler(getApplicationContext());
        registerForContextMenu(contactListview);
        contactListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                longClickedItemIndex=i;
                return false;
            }
        });
        TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec=tabHost.newTabSpec("Creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("List");
        tabSpec.setContent(R.id.tabcontactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);
        final Button addbtn=(Button)findViewById(R.id.addbutton);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nametxt.getText()), String.valueOf(phonetxt.getText()), String.valueOf(emailtxt.getText()), String.valueOf(addresstxt.getText()), imageuri);
                if(!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    Contacts.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nametxt.getText())+ " added to Contact List", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                    Toast.makeText(getApplicationContext(),String.valueOf(nametxt.getText())+" already exists.Please enter a different name",Toast.LENGTH_LONG).show();
            }
        });

        nametxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addbtn.setEnabled(!String.valueOf(nametxt.getText()).trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
            }
        });
        if(dbHandler.getContactsCount()!=0)
            Contacts.addAll(dbHandler.getAllContacts());
        populateList();
    }
        public void onCreateContextMenu(ContextMenu menu,View view,ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu,view,menuInfo);
            //menu.setHeaderIcon(R.drawable.edit_icon);
            //menu.setHeaderTitle("Contact Options");
            menu.add(Menu.NONE, EDIT, Menu.NONE, "Share");
            menu.add(Menu.NONE,DELETE,Menu.NONE,"Delete Contact");
        }
        public boolean onContextItemSelected(MenuItem item){
            switch(item.getItemId()){
                case EDIT:
                    //enableEditMode(Contacts.get(longClickedItemIndex));
                    Toast.makeText(getApplicationContext(),"Share under development",Toast.LENGTH_LONG).show();
                    break;
                case DELETE:
                    dbHandler.deleteContact(Contacts.get(longClickedItemIndex));
                    Contacts.remove(longClickedItemIndex);
                    contactAdapter.notifyDataSetChanged();
                    break;
            }
            return super.onContextItemSelected(item);
        }
        private void enableEditMode(Contact contact){
            TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
            tabHost.setCurrentTab(0);
            nametxt.setText(contact.getname());
            phonetxt.setText(contact.getphone());
            emailtxt.setText(contact.getemail());
            addresstxt.setText(contact.getaddress());
            imageuri = contact.getimageuri();
            contactImage.setImageURI(imageuri);
            Button edit = (Button)findViewById(R.id.addbutton);
            edit.setText("Update");
            }
        private boolean contactExists(Contact contact){
        String name=contact.getname();
        int contactCount=Contacts.size();
        for(int i=0;i<contactCount;i++){
            if(name.compareToIgnoreCase(Contacts.get(i).getname())==0)
                return true;
        }
        return false;
    }
    public void onActivityResult(int reqCode,int resCode,Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageuri=data.getData();
                contactImage.setImageURI(data.getData());
            }
        }
    }
    private void populateList(){
        contactAdapter=new ContactListAdapter();
        contactListview.setAdapter(contactAdapter);
    }
    /*private void addContact(int id,String name,String phone,String email,String address,Uri imageuri){
        Contacts.add(new Contact(id,name,phone,email,address,imageuri));
    }*/
    private class ContactListAdapter extends ArrayAdapter<Contact>{
        public ContactListAdapter(){
            super(MainActivity.this,R.layout.listview_item,Contacts);
        }
        @Override
        public View getView(int position,View view,ViewGroup parent){
            if(view==null)
                view=getLayoutInflater().inflate(R.layout.listview_item,parent,false);
            Contact currentcontact=Contacts.get(position);
            TextView name=(TextView) view.findViewById(R.id.contactname);
            name.setText(currentcontact.getname());
            TextView phone=(TextView)view.findViewById(R.id.phonenumber);
            phone.setText(currentcontact.getphone());
            TextView emailaddr=(TextView)view.findViewById(R.id.emailid);
            emailaddr.setText(currentcontact.getemail());
            TextView addrr=(TextView)view.findViewById(R.id.addressid);
            addrr.setText(currentcontact.getaddress());
            ImageView ivcontactimg=(ImageView)view.findViewById(R.id.ivContactImage);
            ivcontactimg.setImageURI(currentcontact.getimageuri());
            return view;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"Settings pressed",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
