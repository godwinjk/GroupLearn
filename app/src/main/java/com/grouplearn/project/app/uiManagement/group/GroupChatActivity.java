package com.grouplearn.project.app.uiManagement.group;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.file.FileManager;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ChatRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.notification.NotificationManager;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.cloud.download.CloudFileDownloader;
import com.grouplearn.project.cloud.download.OnFileDownloadListener;
import com.grouplearn.project.cloud.upload.CloudFileUploader;
import com.grouplearn.project.cloud.upload.OnFileUploadListener;
import com.grouplearn.project.utilities.AesHelper;
import com.grouplearn.project.utilities.ChatUtilities;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

import static com.grouplearn.project.utilities.AppUtility.getFilePath;


public class GroupChatActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        View.OnClickListener,
        OnFileUploadListener,
        OnFileDownloadListener {

    private static final String TAG = "GroupChatActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
    private static final int ACTION_SELECT_DOC = GLMessage.DOCUMENT;
    private static final int ACTION_SELECT_VIDEO = GLMessage.VIDEO;
    private static final int ACTION_SELECT_IMAGE = GLMessage.IMAGE;
    private static final int ACTION_SELECT_GIF = GLMessage.GIF;
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
        ivSent.setOnClickListener(this);
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
        mChatRecyclerAdapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, int action, View v) {
                if (action == 2) {
                    GLMessage message = (GLMessage) model;
                    download(message);
                } else if (action == 1) {
                    GLMessage message = (GLMessage) model;
                    try {
                        String url = message.getLocalFilePath();
                        if (!TextUtils.isEmpty(url)) {
                            openFile(url);
                        } else {
                            download(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model, int action, View v) {

            }
        });
    }

    private void download(GLMessage message) {
        CloudFileDownloader uploader = new CloudFileDownloader(mContext, message, this);
        uploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void openFile(String url) throws IOException {
        // Create URI

        Uri uri = Uri.parse("file://" + url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // ZIP Files
            intent.setDataAndType(uri, "application/zip");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
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
        model.setMessageType(GLMessage.MESSAG);
        model.setTimeStamp(new BigDecimal(System.currentTimeMillis()).toPlainString());
        model.setTempId(getRandomTempId());
        model.setSentStatus(ChatUtilities.NOT_SENT);
        model.setReadStatus(ChatUtilities.READ);

        mDbHelper.addMessageToDb(model);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sent:
                String message = etChatBox.getText().toString();
                message = message.trim();
                Log.v(TAG, message);
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
            File file = null;
            if (requestCode == ACTION_SELECT_DOC || requestCode == ACTION_SELECT_IMAGE || requestCode == ACTION_SELECT_VIDEO) {
                Uri uri = data.getData();
                if (null != uri) {
                    try {
                        file = new File(getFilePath(this, uri));
                        Log.d(TAG, " PAth " + file.getAbsolutePath());
                        File outPutFile = copyToLocalFolder(file, requestCode);
                        uploadFile(outPutFile, requestCode);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
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

    private void uploadFile(File file, int action) {
        if (file != null) {
            GLMessage message = new GLMessage();
            message.setFile(file);
            String userName = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME);

            message.setMessageBody(AesHelper.encrypt(groupUniqueId, ""));

            message.setLocalFilePath(file.getAbsolutePath());
            message.setSenderName(userName);
            message.setSenderId(myUserId);
            message.setReceiverId(groupUniqueId);
            message.setMessageType(action);
            message.setTimeStamp(new BigDecimal(System.currentTimeMillis()).toPlainString());
            message.setTempId(getRandomTempId());
            message.setSentStatus(ChatUtilities.NOT_SENT_FILE);
            message.setReadStatus(ChatUtilities.READ);

            mDbHelper.addMessageToDb(message);

            CloudFileUploader uploader = new CloudFileUploader(mContext, message, this);
            uploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            uploader.upload();
        }
    }

    private File copyToLocalFolder(File file, int type) {
        FileManager manager = new FileManager();
        return manager.copyToLocal(file, type);
    }

    @Override
    public void onUploadSuccess(GLMessage message) {
        mDbHelper.updateMessageByTempId(message);
        message.setOperationOnProgress(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatRecyclerAdapter.notifyDataSetChanged();
                updateChatList();
            }
        });

    }

    @Override
    public void onUploadInProgress(GLMessage message, int progress) {
        message.setProgress(progress);
        message.setOperationOnProgress(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onUploadFailed(GLMessage message, AppError error) {
        message.setOperationOnProgress(false);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (flEmojicon.getVisibility() == View.VISIBLE)
            setVisibility(flEmojicon, View.GONE);
        else
            super.onBackPressed();

    }

    @Override
    public void onDownloadSuccess(GLMessage message) {
        message.setOperationOnProgress(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatRecyclerAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onDownloadInProgress(GLMessage message, int progress) {
        message.setProgress(progress);
        message.setOperationOnProgress(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDownloadFailed(GLMessage message, AppError error) {
        message.setOperationOnProgress(false);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }
}