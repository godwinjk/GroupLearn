package com.grouplearn.project.app.uiManagement.groupManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ChatRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.models.MessageModel;
import com.grouplearn.project.utilities.ChatUtilities;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import it.moondroid.chatbot.BrainService;
import it.moondroid.chatbot.Constants;


public class GroupChatActivity extends BaseActivity {

    private static final String TAG = "GroupChatActivity";
    String groupUniqueId;
    Toolbar toolbar;
    ImageView ivSent;
    EditText etChatBox;
    Context mContext;
    GroupModel mGroupModel;

    ChatRecyclerAdapter mChatRecyclerAdapter;
    RecyclerView rvChatList;
    ChatDbHelper mDbHelper;
    boolean isGodwinBot = false;
    long myUserId;

    private ArrayList<MessageModel> godwinBotConversation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Intent intent = getIntent();
        mContext = this;

        groupUniqueId = intent.getStringExtra("groupCloudId");

        if (Long.parseLong(groupUniqueId) == -11223344) {
            isGodwinBot = true;
        }
        mGroupModel = new GroupDbHelper(mContext).getGroupInfo(groupUniqueId);
        if (mGroupModel != null)
            toolbar = setupToolbar(mGroupModel.getGroupName(), true);
        else
            toolbar = setupToolbar("Chat", true);

        initializeWidgets();
        registerListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateChatList();

        MessageInteractor.getInstance().getAllMessages(Long.parseLong(groupUniqueId));

        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION_BRAIN_STATUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_BRAIN_ANSWER);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);
        intentFilter.addAction("chat");
        intentFilter.addAction("chatRefresh");

        registerReceiver(chatReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chatReceiver);
    }

    @Override
    public void initializeWidgets() {
        myUserId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);

        mDbHelper = new ChatDbHelper(mContext);
        rvChatList = (RecyclerView) findViewById(R.id.rv_chat_list);
        rvChatList.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

        mChatRecyclerAdapter = new ChatRecyclerAdapter(mContext);
        mChatRecyclerAdapter.setMyUserId(myUserId);

        rvChatList.setAdapter(mChatRecyclerAdapter);

        etChatBox = (EditText) findViewById(R.id.et_chat_box);
        ivSent = (ImageView) findViewById(R.id.iv_sent);

        rvChatList.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        setSentButtonType(1);
    }

    @Override
    public void registerListeners() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(mContext, GroupInfoActivity.class);

                chatIntent.putExtra("groupCloudId", groupUniqueId);
                startActivity(chatIntent);
            }
        });
        ivSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) ivSent.getTag() == 2) {
                    speechRecognizer();
                } else {
                    hideSoftKeyboard();
                    String message = etChatBox.getText().toString();
                    message = message.trim();
                    if (!TextUtils.isEmpty(message)) {
                        addChatToDb(message);
                    }
                }
                if (TextUtils.isEmpty(etChatBox.getText().toString())) {
                    setSentButtonType(1);
                } else {
                    setSentButtonType(2);
                }
            }
        });
        etChatBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideSoftKeyboard();
                    String message = etChatBox.getText().toString();
                    message = message.trim();
                    if (!TextUtils.isEmpty(message)) {
                        addChatToDb(message);
                        hideSoftKeyboard();
                    }
                    return true;
                }
                return false;
            }
        });
        etChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    setSentButtonType(1);
                } else {
                    setSentButtonType(2);
                }
            }
        });
    }

    private void speechRecognizer() {
        SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(mContext);

        final StringBuilder builder = new StringBuilder();
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.e(TAG, "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.e(TAG, "onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.e(TAG, "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.e(TAG, "onEndOfSpeech");
            }

            @Override
            public void onError(int error) {
                Log.e(TAG, "onError" + error);
                DisplayInfo.showToast(mContext, getErrorText(error));
            }

            @Override
            public void onResults(Bundle results) {
                Log.e(TAG, "onResults" + results.toString());
                ArrayList<String> texts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                for (String word : texts) {
                builder.append(texts.get(0) + "\t");
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etChatBox.setText(builder.toString());
                    }
                });
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.e(TAG, "onPartialResults");
                ArrayList<String> texts = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (String word : texts) {
                    builder.append(word + "\t");
                }
                Log.e(TAG, "onPartialResults" + builder.toString());
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.e(TAG, "onEvent");
            }
        });
        Intent recognizerIntent;
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

        recognizer.startListening(recognizerIntent);
//        startActivity(recognizerIntent);
        Log.e(TAG, "START LISTNEING");
        DisplayInfo.showToast(mContext, "Say something loud");
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private void updateChatList() {
        if (!isGodwinBot) {
            ArrayList<MessageModel> messageModels = mDbHelper.getMessages(Long.parseLong(groupUniqueId));
            mChatRecyclerAdapter.setMessageModels(messageModels);
            rvChatList.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
            updateLastActivity();
        } else {
            mChatRecyclerAdapter.setMessageModels(godwinBotConversation);
        }
    }

    private void updateLastActivity() {
        if (mChatRecyclerAdapter != null && mChatRecyclerAdapter.getMessageModels().size() > 0) {
            MessageModel model = mChatRecyclerAdapter.getMessageModels().get(mChatRecyclerAdapter.getMessageModels().size() - 1);
            long currentTimeInMillis = System.currentTimeMillis();
            BigDecimal lastActivity = new BigDecimal(model.getTimeStamp());
            long difference = (currentTimeInMillis - lastActivity.longValue());
            String message = "";
            if (difference <= 1000) {
                message = "a moment ago";
            } else if (difference < 60000) {
                message = ((int) (difference / (1000 * 60))) + " minutes ago";
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy HH:mm");
                message = format.format(new Date(lastActivity.longValue()));
            }
            toolbar.setSubtitle("last activity : " + message);
        }
    }

    BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("chat")) {
                updateChatList();
            }
            if (intent.getAction().equals("chatRefresh")) {
                updateChatList();
                MessageInteractor.getInstance().getAllMessages(Long.parseLong(groupUniqueId));
            }
            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_STATUS)) {
                int status = intent.getIntExtra(Constants.EXTRA_BRAIN_STATUS, 0);
                switch (status) {
                    case Constants.STATUS_BRAIN_LOADING:
                        if (isGodwinBot) {
                            DisplayInfo.showToast(mContext, "Godwin is  loading");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etChatBox.setEnabled(false);
                                    etChatBox.setText("Godwin is loading");
                                    ivSent.setEnabled(false);
                                }
                            });
                        }
                        break;

                    case Constants.STATUS_BRAIN_LOADED:
                        if (isGodwinBot) {
                            DisplayInfo.showToast(mContext, "Godwin is ready");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etChatBox.setEnabled(true);
                                    etChatBox.setText("");
                                    etChatBox.setHint("Ask to Godwin");
                                    ivSent.setEnabled(true);
                                }
                            });
                        }
                        break;
                }
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER)) {
                String answer = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER);
                MessageModel model = new MessageModel();
                model.setMessageBody(answer);
                model.setSenderId(1122);
                model.setSenderName("Aimi");
                godwinBotConversation.add(model);
                mChatRecyclerAdapter.setMessageModels(godwinBotConversation);
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_LOGGER)) {

                String info = intent.getStringExtra(Constants.EXTENDED_LOGGER_INFO);
                if (info != null) {
                    Log.i("EXTENDED_LOGGER_INFO", info);
                }
            }
        }
    };

    private void addChatToDb(String message) {
        etChatBox.setText("");

        AppSharedPreference mPref = new AppSharedPreference(mContext);
        String userName = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME);
        MessageModel model = new MessageModel();
        model.setMessageBody(message);
        model.setSenderName(userName);
        model.setSenderId(myUserId);
        model.setReceiverId(Long.parseLong(groupUniqueId));
        model.setMessageType(0);
        model.setTimeStamp(new BigDecimal(System.currentTimeMillis()).toPlainString());
        model.setTempId(getRandomTempId());
        model.setSentStatus(ChatUtilities.NOT_SENT);
        model.setReadStatus(ChatUtilities.READ);
        if (!isGodwinBot) {
            mDbHelper.addMessageToDb(model);
            MessageInteractor.getInstance().updateMessageToCloud();
        } else {
            Intent brainIntent = new Intent(mContext, BrainService.class);
            brainIntent.setAction(BrainService.ACTION_QUESTION);
            brainIntent.putExtra(BrainService.EXTRA_QUESTION, message);
            startService(brainIntent);
            godwinBotConversation.add(model);
        }
        updateChatList();
    }

    private long getRandomTempId() {
        Random r = new Random();
        long id = r.nextLong();
        if (mDbHelper.isTempIdExist(id) > 0) {
            return getRandomTempId();
        } else
            return id;
    }

    private void rotateAnimation() {
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(200);
        ivSent.startAnimation(animation);
    }

    private void setSentButtonType(int tag) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (tag == 1) {
                ivSent.setTag(2);
                ivSent.setImageResource(R.drawable.mic_128);
            } else {
                ivSent.setTag(1);
                ivSent.setImageResource(R.drawable.sent);
            }

        } else {
            ivSent.setTag(1);
            ivSent.setImageResource(R.drawable.sent);
        }
    }
}