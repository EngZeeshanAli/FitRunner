package com.example.fitrunner.Authentications;

import android.app.ProgressDialog;
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

import com.example.fitrunner.R;
import com.example.fitrunner.UiControllers.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends Fragment implements View.OnClickListener {
    EditText name, email, password;
    Button register;
    TextView signIn;
    String nameS, emailS, passwordS;
    ProgressDialog dialog;

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
        signIn = v.findViewById(R.id.go_to_singin);
        signIn.setOnClickListener(this);
    }

    boolean getFormData() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("REQUIRED");
            return false;
        }
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("REQUIRED");
            return false;
        }
        if (!email.getText().toString().contains("@")) {
            email.setError("Bad Formated Email");
            return false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("REQUIRED");
            return false;
        }
        if (password.getText().length() < 8) {
            password.setError("Must 8 Char");
            return false;
        } else {
            nameS = name.getText().toString();
            emailS = email.getText().toString();
            passwordS = password.getText().toString();
            return true;
        }
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
        switch (v.getId()) {
            case R.id.sign_up:
                if (getFormData()) {
                    signUpNewUser();
                }


                break;
            case R.id.go_to_singin:
                slidleft(new Login());
                break;
        }
    }

    private void signUpNewUser() {
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Processing");
        dialog.setMessage("Please wait while we are processing.");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
                    newUser.sendEmailVerification();

                    User user = new User(nameS, emailS, newUser.getUid());
                    saveUserInfo(user);
                    FirebaseAuth.getInstance().signOut();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(User user) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(user.uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "SignUp Succesfully..", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    email.setText("");
                    password.setText("");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
