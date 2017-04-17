package com.grouplearn.project.app.uiManagement.group;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ChatRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.app.uiManagement.notification.NotificationManager;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.ChatUtilities;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;


public class GroupChatActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        View.OnClickListener {

    private static final String TAG = "GroupChatActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
    private static final int ACTION_SELECT_DOC = 201;
    private static final int ACTION_SELECT_VIDEO = 202;
    private static final int ACTION_SELECT_IMAGE = 203;
    private long groupUniqueId;

    private Toolbar toolbar;
    private ImageView ivSent;

    private EmojiconEditText etChatBox;
    private RecyclerView rvChatList;
    private Context mContext;
    private GLGroup mGroupModel;
    private ImageView ivGroupIcon, ivKeyboard, ivImage, ivEmojicon, ivVideo, ivDoc;
    private FrameLayout flEmojicon;
    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatDbHelper mDbHelper;
    private AppSharedPreference mPref;

    private long myUserId;

    private boolean isZoomAnimation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Intent intent = getIntent();
        mContext = this;
        mPref = new AppSharedPreference(mContext);

        groupUniqueId = intent.getLongExtra("groupCloudId", -1);

        mGroupModel = new GroupDbHelper(mContext).getGroupInfo(groupUniqueId);
        if (mGroupModel != null)
            toolbar = setupToolbar(mGroupModel.getGroupName(), true);
        else
            toolbar = setupToolbar("Chat", true);

        initializeWidgets();
        registerListeners();
        setEmojiconFragment(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager.getInstance().cancelNotification();
        updateChatList();

        MessageInteractor.getInstance().getAllMessages(groupUniqueId);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("chat");
        intentFilter.addAction("chatRefresh");

        registerReceiver(chatReceiver, intentFilter);

        mGroupModel = new GroupDbHelper(mContext).getGroupInfo(groupUniqueId);
        String imageUri = mGroupModel.getIconUrl();
        setupGroupIcon(imageUri);
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

        etChatBox = (EmojiconEditText) findViewById(R.id.et_chat_box);

        ivSent = (ImageView) findViewById(R.id.iv_sent);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        ivEmojicon = (ImageView) findViewById(R.id.iv_emojiocn);
        ivKeyboard = (ImageView) findViewById(R.id.iv_keyboard);
        ivDoc = (ImageView) findViewById(R.id.iv_doc);
        ivVideo = (ImageView) findViewById(R.id.iv_video);

        ivGroupIcon = (ImageView) findViewById(R.id.iv_group_icon);
        rvChatList.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        flEmojicon = (FrameLayout) findViewById(R.id.fl_emojicons);

        if (mPref.getBooleanPrefValue(PreferenceConstants.IS_ENTER_KEY_SEND_MESSAGE)) {
            etChatBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etChatBox.setMaxLines(1);
            etChatBox.setImeOptions(EditorInfo.IME_ACTION_SEND);
        } else {
            etChatBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            etChatBox.setMaxLines(5);
            etChatBox.setImeOptions(EditorInfo.IME_ACTION_NONE);
        }
        String imageUri = mGroupModel.getIconUrl();
        setupGroupIcon(imageUri);
    }

    private void setupGroupIcon(String imageUri) {
        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(ivGroupIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            ivGroupIcon.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(etChatBox, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(etChatBox);
    }

    @Override
    public void registerListeners() {
        ivKeyboard.setOnClickListener(this);
        ivVideo.setOnClickListener(this);
        ivDoc.setOnClickListener(this);
        ivImage.setOnClickListener(this);
        ivEmojicon.setOnClickListener(this);

        etChatBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (flEmojicon.getVisibility() == View.VISIBLE) {
                    openSoftKeyBoard();
                    setVisibility(flEmojicon, View.GONE);
                }
                return false;
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(mContext, GroupInfoActivity.class);

                chatIntent.putExtra("groupCloudId", groupUniqueId);
                startActivity(chatIntent);
            }
        });
        etChatBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (mPref.getBooleanPrefValue(PreferenceConstants.IS_ENTER_KEY_SEND_MESSAGE)) {
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
                return false;
            }
        });
    }

    private void setVisibility(final View v, final int visibility) {
        int time = 0;
        if (visibility == View.VISIBLE)
            time = 150;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setVisibility(visibility);
            }
        }, time);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
        flEmojicon.setVisibility(View.GONE);
    }

    private void updateChatList() {
        ArrayList<GLMessage> messageModels = mDbHelper.getMessages(groupUniqueId);
        mChatRecyclerAdapter.setMessageModels(messageModels);
        rvChatList.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        updateLastActivity();
    }

    private void updateLastActivity() {
        if (mChatRecyclerAdapter != null && mChatRecyclerAdapter.getMessageModels().size() > 0) {
            GLMessage model = mChatRecyclerAdapter.getMessageModels().get(mChatRecyclerAdapter.getMessageModels().size() - 1);
            long currentTimeInMillis = System.currentTimeMillis();
            if (TextUtils.isEmpty(model.getTimeStamp())) {
                return;
            }
            double timestamp = Double.parseDouble(model.getTimeStamp());

            long lastActivity = (long) Math.abs(timestamp);
            int numberOfDigit = (int) (Math.log10(lastActivity) + 1);
            if (numberOfDigit == 10)
                lastActivity *= 1000;
            long difference = (currentTimeInMillis - lastActivity);
            String message = "";
            if (difference <= 1000) {
                message = "a moment ago";
            } else if (difference < /*59 **/ 60 * 1000) {
                message = ((int) (difference / (1000 * 60))) + " minutes ago";
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                message = format.format(new Date(lastActivity));
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
                MessageInteractor.getInstance().getAllMessages(groupUniqueId);
            }
        }
    };

    private void addChatToDb(String message) {
        etChatBox.setText("");

        String userName = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME);
        GLMessage model = new GLMessage();
        model.setMessageBody(message);
        model.setSenderName(userName);
        model.setSenderId(myUserId);
        model.setReceiverId(groupUniqueId);
        model.setMessageType(0);
        model.setTimeStamp(new BigDecimal(System.currentTimeMillis()).toPlainString());
        model.setTempId(getRandomTempId());
        model.setSentStatus(ChatUtilities.NOT_SENT);
        model.setReadStatus(ChatUtilities.READ);

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
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(500);
        ivSent.startAnimation(animation);
    }

    private void zoomAnimation(float rms) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isZoomAnimation = true;
            }
        }, 200);
        if (isZoomAnimation) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(.5f, rms / 10, .5f, rms / 10, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
            scaleAnimation.setInterpolator(new OvershootInterpolator());
            scaleAnimation.setDuration(200);
            ivSent.startAnimation(scaleAnimation);
            isZoomAnimation = false;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sent:
                String message = etChatBox.getText().toString();
                message = message.trim();
                if (!TextUtils.isEmpty(message)) {
                    addChatToDb(message);
                }
                break;
            case R.id.toolbar:
                Intent chatIntent = new Intent(mContext, GroupInfoActivity.class);
                chatIntent.putExtra("groupCloudId", groupUniqueId);
                startActivity(chatIntent);
                break;
            case R.id.iv_keyboard:
                openSoftKeyBoard();
                setVisibility(flEmojicon, View.GONE);
                break;
            case R.id.iv_emojiocn:
                hideSoftKeyboard();
                setVisibility(flEmojicon, View.VISIBLE);
                break;
            case R.id.iv_image:
                hideSoftKeyboard();
                startActivity(ACTION_SELECT_IMAGE);
                break;
            case R.id.iv_video:
                hideSoftKeyboard();
                startActivity(ACTION_SELECT_VIDEO);
                break;
            case R.id.iv_doc:
                hideSoftKeyboard();
                startActivity(ACTION_SELECT_DOC);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String filePath = null;
            if (requestCode == ACTION_SELECT_DOC) {
                filePath = data.getData().getPath();
            } else if (requestCode == ACTION_SELECT_IMAGE) {
                Uri uri = data.getData();
                if (null != uri) {
                    filePath = uri.getPath();
                }
            } else if (requestCode == ACTION_SELECT_VIDEO) {
                Uri uri = data.getData();
                if (null != uri) {
                    filePath = uri.getPath();
                }
            }
            Log.d(TAG, " PAth " + filePath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DisplayInfo.showToast(mContext, "Please try again.");
    }

    private boolean checkPermission() {
        boolean status = false;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else status = true;
        } else status = true;
        return status;
    }

    private void startActivity(int action) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (action == ACTION_SELECT_DOC) {
            intent.setType("file/*");
        } else if (action == ACTION_SELECT_IMAGE) {
            intent.setType("image/*");
        } else if (action == ACTION_SELECT_VIDEO) {
            intent.setType("video/*");
        }
        if (checkPermission()) {
            startActivityForResult(Intent.createChooser(intent, "GroupLearn"), action);
        }
    }
}