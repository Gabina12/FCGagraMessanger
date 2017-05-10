package ge.bondx.fcgagramessanger;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ge.bondx.fcgagramessanger.adapters.ContactAdapter;
import ge.bondx.fcgagramessanger.models.Contact;
import ge.bondx.fcgagramessanger.models.PrivateChatMessage;
import ge.bondx.fcgagramessanger.models.ResultMessage;
import ge.bondx.fcgagramessanger.models.sendData;
import ge.bondx.fcgagramessanger.service.JSONHttpClient;
import ge.bondx.fcgagramessanger.service.ServiceUrl;
import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.LoadMoreMessagesListener;
import it.slyce.messaging.listeners.OnOptionSelectedListener;
import it.slyce.messaging.listeners.UserClicksAvatarPictureListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.GeneralOptionsMessage;
import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import it.slyce.messaging.message.messageItem.MessageItem;
import it.slyce.messaging.view.text.FontEditText;

public class MessagingActivity extends AppCompatActivity {

    private Contact contact;
    private Contact me;
    private String myImageUrl;
    private PrivateChatMessage[] messageList;
    private int take = 10;
    private int skip = 0;
    private  ArrayList<Message> messages;
    private  RecyclerView recyclerView;

    private ArrayList<Message> GetMessages(){

        ArrayList<Message> data = new ArrayList<>();

        if(messageList.length == 0) return  data;

        for (PrivateChatMessage msg: messageList ) {
            Message message;
            TextMessage textMessage = new TextMessage();
            textMessage.setText(msg.getMessageText()); // +  ": " + latin[(int) (Math.random() * 10)]);
            textMessage.setUserId(msg.getMsgForm());
            long startDate = new Long(0);
            try {
                String dateString = msg.getCreateDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date date = sdf.parse(dateString);

                startDate = date.getTime();
                textMessage.setDate(startDate);
                //textMessage.setDate(System.currentTimeMillis());


            } catch (ParseException e) {
                e.printStackTrace();
            }

            message = textMessage;
            if(msg.getMsgForm().toString().equals(contact.getEmail())) {
                message.setSource(MessageSource.EXTERNAL_USER);
            }
            else {
                message.setSource(MessageSource.LOCAL_USER);
            }
            message.setDate(startDate);
            //message.setDate(System.currentTimeMillis());

            message.setAvatarUrl(msg.getFromImage());

            message.setDisplayName(msg.getMsgForm());

            data.add(message);
        }
        return data;
    }

    SlyceMessagingFragment slyceMessagingFragment;

    private boolean hasLoadedMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));

        messageList = new PrivateChatMessage[0];

        contact = (Contact) getIntent().getSerializableExtra("Contact");
        me = (Contact) getIntent().getSerializableExtra("me");
        myImageUrl = me.getImageUrl();
        getSupportActionBar().setTitle(contact.getFirstName() + " " + contact.getLastName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setSubtitle("online");


        if(contact.getNotificationId() != 0){
            removeNotification(contact.getNotificationId());
        }

        hasLoadedMore = false;

        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setDefaultAvatarUrl(myImageUrl);
        slyceMessagingFragment.setDefaultDisplayName(me.getFirstName() + " " + me.getLastName());
        slyceMessagingFragment.setDefaultUserId(me.getEmail());

        slyceMessagingFragment.setPictureButtonVisible(false);

        slyceMessagingFragment.setUserClicksAvatarPictureListener(new UserClicksAvatarPictureListener() {
            @Override
            public void userClicksAvatarPhoto(String userId) {
                Toast.makeText(MessagingActivity.this,userId,Toast.LENGTH_SHORT).show();
            }
        });

        View parentLayout = findViewById(R.id.fragment_for_slyce_messaging);
        slyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {
            @Override
            public void onUserSendsTextMessage(String text) {
                new SendMessage(MessagingActivity.this).execute(text);
            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {
                Log.d("inf", "******************************** " + imageUri);
            }
        });

        slyceMessagingFragment.setLoadMoreMessagesListener(new LoadMoreMessagesListener() {
            @Override
            public List<Message> loadMoreMessages() {
                if (!hasLoadedMore) {
                    sendData data = new sendData();
                    data.setFrom(contact.getEmail());
                    data.setTo(me.getEmail());
                    data.setTake(take);
                    data.setSkip(skip);
                    messageList = new PrivateChatMessage[0];
                    JSONHttpClient jsonHttpClient = new JSONHttpClient();
                    messageList = jsonHttpClient.PostObject(ServiceUrl.MESSAGES,data,PrivateChatMessage[].class);
                    messages = GetMessages();
                    skip+=take;
                    return messages;
                } else {
                    slyceMessagingFragment.setMoreMessagesExist(false);
                    return messages;
                }
            }
        });

        recyclerView = (RecyclerView)slyceMessagingFragment.getActivity().findViewById(R.id.slyce_messaging_recycler_view);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(
                                    recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        slyceMessagingFragment.setMoreMessagesExist(true);

    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("from");
            if(from.equals(contact.getEmail())){
                String msg = intent.getStringExtra("message");
                Integer msg_id = intent.getIntExtra("message_id",0);
                if(msg != ""){
                    Message message;
                    TextMessage textMessage = new TextMessage();
                    textMessage.setText(msg);
                    textMessage.setUserId(from);
                    message = textMessage;
                    message.setSource(MessageSource.EXTERNAL_USER);

                    message.setAvatarUrl(contact.getImageUrl());

                    message.setDisplayName(contact.getFirstName());

                    removeNotification(msg_id);
                    slyceMessagingFragment.addNewMessage(message);
                }
            }
        }
    };

    private void removeNotification(int msg_id){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(msg_id);
    }

    private void updateUi(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class LoadMessages extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        Activity context;
        public LoadMessages(Activity act){
            context = act;
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            sendData data = new sendData();
            data.setFrom(contact.getEmail());
            data.setTo(me.getEmail());
            data.setTake(take);
            data.setSkip(skip);
            messageList = new PrivateChatMessage[0];
            JSONHttpClient jsonHttpClient = new JSONHttpClient();
            messageList = jsonHttpClient.PostObject(ServiceUrl.MESSAGES,data,PrivateChatMessage[].class);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
            progressDialog = new ProgressDialog(MessagingActivity.this);
            progressDialog.setMessage("გთხოვთ დაელოდოთ...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Message> mess = GetMessages();
                    hasLoadedMore = messageList.length != 0;
                    skip += mess.size();
                    if(mess.size() > 0)
                        slyceMessagingFragment.addNewMessages(mess);


                }
            });
        }

        /*
        try {
            Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                TextMessage textMessage = new TextMessage();
                textMessage.setText("Another message...");
                textMessage.setAvatarUrl(contact.getImageUrl());
                textMessage.setDisplayName(contact.getFirstName() + " " + contact.getLastName());
                textMessage.setUserId("LP");
                textMessage.setDate(new Date().getTime());
                textMessage.setSource(MessageSource.EXTERNAL_USER);
                slyceMessagingFragment.addNewMessage(textMessage);
            }
        }, 3, 3, TimeUnit.SECONDS);*/
    }

    class SendMessage extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        Activity context;
        public SendMessage(Activity act){
            context = act;
        }

        @Override
        protected String doInBackground(String... params) {
            sendData data = new sendData();
            data.setFrom(me.getEmail());
            data.setTo(contact.getEmail());
            data.setMessage(params[0]);
            JSONHttpClient jsonHttpClient = new JSONHttpClient();
            ResultMessage msg = jsonHttpClient.PostObject(ServiceUrl.SEND_MESSAGE, data,ResultMessage.class);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
            /*progressDialog = new ProgressDialog(MessagingActivity.this);
            progressDialog.setMessage("გთხოვთ დაელოდოთ...");
            progressDialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            //progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
