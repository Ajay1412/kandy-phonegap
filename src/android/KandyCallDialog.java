package com.kandy.phonegap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.genband.kandy.api.services.calls.IKandyCall;
import com.genband.kandy.api.services.calls.KandyView;
import org.apache.cordova.CallbackContext;

/**
 * The native (video) call {@link android.app.AlertDialog}.
 *
 * @author kodeplusdev
 * @version 1.0.0
 */
public class KandyCallDialog extends Dialog {
    private static final String LCAT = KandyCallDialog.class.getSimpleName();

    private Button uiHangupButton;
    private ToggleButton uiHoldTButton;
    private ToggleButton uiMuteTButton;
    private ToggleButton uiVideoTButton;

    private KandyView uiRemoteVideoView;
    private KandyView uiLocalVideoView;

    private boolean mHoldState = false;
    private boolean mMuteState = false;
    private boolean mVideoSharingState = true;

    private IKandyCall _currentCall;

    private KandyVideoCallDialogListener _kandyVideoCallDialogListener;

    private CallbackContext _callbackContext;

    private KandyUtils utils = KandyUtils.getInstance(null);

    private View.OnClickListener onHangupButtonClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            doHangup(_currentCall);
        }
    };

    private CompoundButton.OnCheckedChangeListener onHoldTButtonClicked = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switchHoldState(_currentCall, isChecked);
        }
    };

    private CompoundButton.OnCheckedChangeListener onMuteTButtonClicked = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switchMuteState(_currentCall, isChecked);
        }
    };

    private CompoundButton.OnCheckedChangeListener onVideoTButtonClicked = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switchVideoSharing(_currentCall, isChecked);
        }
    };

    /**
     * {@inheritDoc}
     *
     * @param context The {@link android.content.Context} to use.
     */
    public KandyCallDialog(Context context) {
        super(context);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(utils.getLayout("kandy_call_dialog"));
        initializeViews();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
    }

    /**
     * Initialize the dialog.
     */
    private void initializeViews() {
        uiHangupButton = (Button) findViewById(utils.getId("kandy_calls_hangup_button"));
        uiHangupButton.setOnClickListener(onHangupButtonClicked);

        uiHoldTButton = (ToggleButton) findViewById(utils.getId("kandy_calls_hold_tbutton"));
        uiHoldTButton.setOnCheckedChangeListener(onHoldTButtonClicked);
        uiHoldTButton.setChecked(mHoldState);

        uiMuteTButton = (ToggleButton) findViewById(utils.getId("kandy_calls_mute_tbutton"));
        uiMuteTButton.setOnCheckedChangeListener(onMuteTButtonClicked);
        uiMuteTButton.setChecked(mMuteState);

        uiVideoTButton = (ToggleButton) findViewById(utils.getId("kandy_calls_video_tbutton"));
        uiVideoTButton.setChecked(mVideoSharingState);
        uiVideoTButton.setOnCheckedChangeListener(onVideoTButtonClicked);

        uiRemoteVideoView = (KandyView) findViewById(utils.getId("kandy_calls_video_view"));
        uiLocalVideoView = (KandyView) findViewById(utils.getId("kandy_calls_local_video_view"));
    }

    /**
     * Hangup the current call.
     *
     * @param call The current call.
     */
    private void doHangup(IKandyCall call) {
        if (call == null) {
            _callbackContext.error(utils.getString("kandy_calls_invalid_hangup_text_msg"));
            return;
        }

        _kandyVideoCallDialogListener.hangup();
        this.dismiss();
    }

    /**
     * Switch mute state of the current call.
     *
     * @param call The current call.
     * @param state The mute state of current call.
     */
    private void switchMuteState(IKandyCall call, boolean state) {
        if (call == null) {
            uiMuteTButton.setChecked(false);
            _callbackContext.error(utils.getString("kandy_calls_invalid_mute_call_text_msg"));
            return;
        }

        mMuteState = state;

        _kandyVideoCallDialogListener.switchMuteState(state);
    }

    /**
     * Switch hold state of the current call.
     *
     * @param call The current call.
     * @param state The hold state of current call.
     */
    private void switchHoldState(IKandyCall call, boolean state) {
        if (call == null) {
            uiHoldTButton.setChecked(false);
            _callbackContext.error(utils.getString("kandy_calls_invalid_hold_text_msg"));
            return;
        }

        mHoldState = state;

        _kandyVideoCallDialogListener.switchHoldState(state);
    }

    /**
     * Switch video sharing state of the current call.
     *
     * @param call The current call.
     * @param state The video sharing state of current video.
     */
    private void switchVideoSharing(IKandyCall call, boolean state) {
        if (call == null) {
            uiVideoTButton.setChecked(false);
            _callbackContext.error(utils.getString("kandy_calls_invalid_video_call_text_msg"));
            return;
        }

        mVideoSharingState = state;

        _kandyVideoCallDialogListener.switchVideoSharingState(state);
    }

    /**
     * Set the current call and the video views for this dialog
     *
     * @param call The current call.
     */
    public void setKandyCall(IKandyCall call) {
        _currentCall = call;
        _currentCall.setLocalVideoView(uiLocalVideoView);
        _currentCall.setRemoteVideoView(uiRemoteVideoView);
    }

    /**
     * Register the call listener from Kandy plugin.
     *
     * @param listener The callback listener.
     */
    public void setKandyVideoCallListener(KandyVideoCallDialogListener listener) {
        _kandyVideoCallDialogListener = listener;
    }

    /**
     * Register the callback from Kandy plugin.
     *
     * @param callbackContext The current {@link CallbackContext} to use.
     */
    public void setKandyCallbackContext(CallbackContext callbackContext) {
        _callbackContext = callbackContext;
    }

    /**
     * The listener interface of the video call dialog
     */
    public interface KandyVideoCallDialogListener {

        /**
         * Hangup the current call.
         */
        void hangup();

        /**
         * Switch mute state of the current call.
         *
         * @param state The mute state.
         */
        void switchMuteState(boolean state);

        /**
         * Switch hold state of current call.
         *
         * @param state The hold state.
         */
        void switchHoldState(boolean state);

        /**
         * Switch video sharing state of current call.
         *
         * @param state The video sharing state.
         */
        void switchVideoSharingState(boolean state);
    }
}
