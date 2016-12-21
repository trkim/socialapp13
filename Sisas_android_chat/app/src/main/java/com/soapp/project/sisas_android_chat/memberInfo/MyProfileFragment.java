package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.member.LoginActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by eelhea on 2016-10-14.
 */
public class MyProfileFragment extends Fragment {

    SharedPreferences memberSession;

    CircleImageView profile_pic;
    private static final int PICK_FROM_ALBUM = 1;
    Bitmap photo;
    String image="";

    TextView profile_name;
    TextView profile_email;
    TextView profile_major;
    TextView profile_interest;
    TextView profile_coupon_num;

    Button btn_profile_update;
    Button btn_logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_info_profile_frag, container, false);

        memberSession = getActivity().getSharedPreferences("MemberSession", Context.MODE_PRIVATE);

        profile_pic = (CircleImageView)view.findViewById(R.id.profile_pic);
        profile_name = (TextView)view.findViewById(R.id.profile_name);
        profile_email = (TextView)view.findViewById(R.id.profile_email);
        profile_major = (TextView)view.findViewById(R.id.profile_major);
        profile_interest = (TextView)view.findViewById(R.id.profile_interest);
        profile_coupon_num = (TextView)view.findViewById(R.id.profile_coupon_num);

        profile_name.setText(Member.getInstance().getName());
        profile_email.setText(Member.getInstance().getEmail());
        profile_major.setText(Member.getInstance().getMajor());
        profile_interest.setText(Member.getInstance().getCategory().substring(1));
        profile_coupon_num.setText(String.valueOf(Member.getInstance().getCoupon()));

        btn_profile_update = (Button)view.findViewById(R.id.btn_profile_update);
        btn_logout = (Button)view.findViewById(R.id.btn_logout);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(getActivity())
                        .setTitle("업로드할 이미지 선택")
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        btn_profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent member_update_intent = new Intent(getActivity(), MemberUpdateActivity.class);
                startActivity(member_update_intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        try {
            getProfilePicFromServer();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    private void getProfilePicFromServer(){
        final String URL = "http://52.78.157.250:3000/get_member";

        Map<String, String> param = new HashMap<String, String>();
        param.put("email", Member.getInstance().getEmail());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast.makeText(getActivity(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else  {
                        if(response.getString("profile_pic").equals("")){
                            profile_pic.setImageResource(R.mipmap.ic_profile);
                        } else {
                            profile_pic.setImageBitmap(StringToBitmap(response.getString("profile_pic")));
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && null != data) {
            Uri mImageCaptureUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(mImageCaptureUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            uriToBitmap(data.getData());
            if (picturePath != null) {
                profile_pic.setImageURI(data.getData());
                try {
                    setProfileToServer();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            photo = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProfileToServer(){
        final String URL = "http://52.78.157.250:3000/insert_profile";

        image = BitmapToString(photo);
        String email = Member.getInstance().getEmail();

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("email", email);
        param.put("profile_pic", image);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(getActivity(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        } else if(response.getString("result").equals("success")){
                            Toast.makeText(getActivity(), "프로필 사진이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    protected void showLogoutDialog(){
        LayoutInflater current_layout = LayoutInflater.from(getActivity());
        View dialog_view = current_layout.inflate(R.layout.activity_logout_member, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(getActivity());
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SharedPreferences.Editor editor = memberSession.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                    Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alert_dialog_builder.create();
        alert.show();
    }

    protected void showLeaveDialog(){
        LayoutInflater current_layout = LayoutInflater.from(getActivity());
        View dialog_view = current_layout.inflate(R.layout.activity_leave_member, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(getActivity());
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteMemberFromServer(Member.getInstance().getEmail());

                    SharedPreferences.Editor editor = memberSession.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alert_dialog_builder.create();
        alert.show();
    }

    private void deleteMemberFromServer(final String email){
        final String URL = "http://52.78.157.250:3000/delete_member";

        Map<String, Object> delete_member_param = new HashMap<String, Object>();
        delete_member_param.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(delete_member_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.toString().contains("result")){
                                if(response.getString("result").equals("fail")){
                                    Toast.makeText(getActivity(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else  {
                                Toast.makeText(getActivity(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }
}
