package com.soapp.project.sisas_android_chat.studyInRoom;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eelhea on 2016-11-28.
 */


public class OtChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText mInputMessageView;
    private RecyclerView mMessagesView;
    private OnFragmentInteractionListener mListener;
    private List<OtChatMsgs> mMessages = new ArrayList<OtChatMsgs>();
    private String mUsername;
    private RecyclerView.Adapter mAdapter;



    int room_id;

    private Socket socket;
    {
        try{
            socket = IO.socket("http://52.78.157.250:3000");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtChatFragment newInstance(String room_id) {
        OtChatFragment fragment = new OtChatFragment();
        Bundle args = new Bundle();
        args.putString("room_id", room_id);
        Log.e("frag instance room_id", String.valueOf(room_id));

        fragment.setArguments(args);
        return fragment;
    }

    public OtChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String username = Member.getInstance().getName();

        Log.e("frag oncreate", "frag oncreate");

        Bundle bundle_arg = getArguments();
        if(bundle_arg != null) {
            room_id = Integer.parseInt(bundle_arg.getString("room_id"));
            Log.e("frag oncreate room_id", String.valueOf(room_id));
        }

        /*socket.emit("login", username);
        socket.emit("setting_roomid", room_id);*/
        JSONObject json = new JSONObject();
        try{
            json.put("type", "join");
            json.put("room_id", room_id);
            json.put("username", username);
        }catch (Exception e){
            e.printStackTrace();
        }

        socket.emit("joinroom", json);
        //socket.on("system", handleIncomingMessages);
        socket.on("new message", handleIncomingMessages);
        socket.connect();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        Log.e("frag onCreateView", "frag onCreateView");

        if(view == null){
            view = (ViewGroup) inflater.inflate(R.layout.study_in_room_ot_chat_frag, container, false);
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new OtChatMsgsAdapter(activity, mMessages);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);

        Log.e("frag onViewCreated", "frag onViewCreated");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }

    private void sendMessage(){
        String message = mInputMessageView.getText().toString().trim();
        String username = Member.getInstance().getName();
        if(TextUtils.isEmpty((message))){
            mInputMessageView.requestFocus();
            return;
        }
        mInputMessageView.setText("");
        addMessage(username, message);
        JSONObject json = new JSONObject();

        try {
            json.put("message", message);
            json.put("room_id", String.valueOf(room_id));
            json.put("username",username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("new message", json);
    }

    private void addMessage(String username, String message) {
        mMessages.add(new OtChatMsgs.Builder(OtChatMsgs.TYPE_MESSAGE)
                .username(username).message(message).build());
         mAdapter = new OtChatMsgsAdapter(getContext(), mMessages);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                        Log.e("emitter username :",username);
                        Log.e("emitter message : ",message);
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);

                }
            });
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
