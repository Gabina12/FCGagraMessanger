package ge.bondx.fcgagramessanger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ge.bondx.fcgagramessanger.adapters.ContactAdapter;
import ge.bondx.fcgagramessanger.models.Contact;
import ge.bondx.fcgagramessanger.service.JSONHttpClient;
import ge.bondx.fcgagramessanger.service.ServiceUrl;

import static android.view.View.TEXT_ALIGNMENT_VIEW_START;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private  Contact me;
    private ArrayList<Contact> contactList;
    private ListView listView;
    private TextView myName;
    private TextView myPosition;
    private ImageView myImage;

    private MaterialSearchBar searchBar;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setFitsSystemWindows(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        contactList = new ArrayList<>();

        listView = (ListView)findViewById(R.id.contactList);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(MainActivity.this,MessagingActivity.class);
               Contact contact = (Contact) contactList.get(position);
               intent.putExtra("Contact", (Serializable)contact);
               intent.putExtra("me",(Serializable) me);

                View v = listView.getChildAt(position -
                        listView.getFirstVisiblePosition());

                if(v == null)
                    return;

                TextView messageTxt = (TextView) v.findViewById(R.id.artist);
                messageTxt.setTypeface(null, Typeface.NORMAL);

               startActivity(intent);
            }
        });

        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setHint("Custom hint");
        searchBar.setTextColor(R.color.colorPrimaryDark);
        searchBar.setSpeechMode(false);
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                String s = enabled ? "enabled" : "disabled";
                if(s == "disabled"){
                    MainActivity.this.adapter.getFilter().filter("");
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                MainActivity.this.adapter.getFilter().filter(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode){
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.openDrawer(Gravity.LEFT);
                        break;
                    case MaterialSearchBar.BUTTON_SPEECH:
                        //openVoiceRecognizer();
                        break;
                }
            }
        });
        //enable searchbar callbacks
        //searchBar.setOnSearchActionListener();
       // searchBar.setLastSuggestions(list);
        //Inflate menu and setup OnMenuItemClickListener
        //searchBar.inflateMenu(R.menu.main);
        //searchBar.getMenu().setOnMenuItemClickListener(this);

        myName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.myName);
        myPosition = (TextView)navigationView.getHeaderView(0).findViewById(R.id.myPosition);
        myImage = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView);

        me = (Contact) getIntent().getSerializableExtra("Me");
        myName.setText(me.getFirstName() + " " + me.getLastName());
        myPosition.setText(me.getPosition());
        Glide.with(getApplicationContext())
                .load(me.getImageUrl())
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(myImage);


        new LoadContacts(this).execute();
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUi(context,intent);
        }
    };

    private void updateUi(Context context, Intent intent){
        String from = intent.getStringExtra("from");
        String msg = intent.getStringExtra("message");
        int msg_id = intent.getIntExtra("message_id",0);
        Integer index = 0;
        for(Contact d : contactList){
            if(d.getEmail() != null && d.getEmail().contains(from)) {
                d.setNotificationId(msg_id);
                break;
            }
            index++;
        }


        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if(v == null)
            return;

        TextView messageTxt = (TextView) v.findViewById(R.id.artist);
        messageTxt.setTypeface(null, Typeface.BOLD);
        messageTxt.setText(msg);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class LoadContacts extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        Activity context;
        public LoadContacts(Activity act){
            context = act;
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            JSONHttpClient jsonHttpClient = new JSONHttpClient();
            Contact[] products = jsonHttpClient.Get(ServiceUrl.CONTACTS, nameValuePairs, Contact[].class);

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            FirebaseMessaging.getInstance().subscribeToTopic(me.getCategory());
            SharedPreferences pref = getSharedPreferences("perf_general",MODE_PRIVATE);
            String username = pref.getString("username", null);
            jsonHttpClient.GetService(ServiceUrl.TOKEN + "?user="+username+"&token=" + refreshedToken);

            if (products.length > 0) {

                for (Contact product : products) {
                    contactList.add(product);
                }

            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("გთხოვთ დაელოდოთ...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter =new ContactAdapter(context, contactList);
                    listView.setAdapter(adapter);
                }
            });
        }
    }
}
