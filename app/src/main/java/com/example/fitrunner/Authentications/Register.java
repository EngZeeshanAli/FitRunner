package com.example.fitrunner.Authentications;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitrunner.MainActivity;
import com.example.fitrunner.R;

public class Register extends Fragment implements View.OnClickListener {
    EditText name, email, password;
    Button register;
    TextView signIn;
    String nameS,emailS,passwordS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        name = v.findViewById(R.id.user_name);
        email = v.findViewById(R.id.email_register);
        password = v.findViewById(R.id.password_register);
        register = v.findViewById(R.id.sign_up);
        register.setOnClickListener(this);
        signIn=v.findViewById(R.id.go_to_singin);
        signIn.setOnClickListener(this);
    }

    void getFormData(){
        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("REQUIRED");
            return;
        }
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("REQUIRED");
            return;
        }
        if(!email.getText().toString().contains("@")){
            email.setError("Bad Formated Email");
            return;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("REQUIRED");
            return;
        }
        if (password.getText().length()<8){
            password.setError("Must 8 Char");
            return;
        }
        nameS=name.getText().toString();
        emailS=email.getText().toString();
        passwordS=password.getText().toString();
    }

    public void slidleft(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.login_register, fragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up:
                getFormData();
                Toast.makeText(getContext(), "Sign Up", Toast.LENGTH_SHORT).show();

                break;
            case R.id.go_to_singin:
               slidleft(new Login());
                break;
        }
    }

}
