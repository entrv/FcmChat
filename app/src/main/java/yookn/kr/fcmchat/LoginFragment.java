package yookn.kr.fcmchat;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginFragment extends Fragment {
    SharedPreferences prefs;
    EditText name, mobno;
    Button login;
    List<NameValuePair> params;
    ProgressDialog progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);

        name = (EditText)view.findViewById(R.id.name);
        mobno = (EditText)view.findViewById(R.id.mobno);
        login = (Button)view.findViewById(R.id.log_btn);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Registering ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();

                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("REG_FROM", mobno.getText().toString());
                edit.putString("FROM_NAME", name.getText().toString());
                edit.commit();
                new Login().execute();
            }
        });

        return view;
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, String> {

        // コンストラクタ
        public MyAsyncTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }
    }



    private class Login extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {




            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name.getText().toString()));
            params.add(new BasicNameValuePair("mobno", mobno.getText().toString()));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));
            Log.d("entrv","login name >>>" + name.getText().toString());
            Log.d("entrv","login name >>>" + mobno.getText().toString());
            Log.d("entrv","login name >>>" + prefs.getString("REG_ID",""));
            JSONObject jObj = json.getJSONFromUrl("http://10.0.0.1:8080/login",params);
            return jObj;



        }
        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sucessfully Registered")) {
                    Fragment reg = new UserFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
                    Toast.makeText(getActivity(),res, Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}