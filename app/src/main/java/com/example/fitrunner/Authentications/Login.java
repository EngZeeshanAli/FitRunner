package com.example.fitrunner.Authentications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitrunner.DashBoard;
import com.example.fitrunner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends Fragment implements View.OnClickListener {
    EditText emailE, passwordE;
    String email, password;
    Button signIn;
    TextView forgot, signUp;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        emailE = v.findViewById(R.id.email_sign_in);
        passwordE = v.findViewById(R.id.password_sign_in);
        signIn = v.findViewById(R.id.sign_in);
        signIn.setOnClickListener(this);
        forgot = v.findViewById(R.id.forgot);
        forgot.setOnClickListener(this);
        signUp = v.findViewById(R.id.go_to_singup);
        signUp.setOnClickListener(this);
    }

    private boolean getFormData() {

        if (TextUtils.isEmpty(emailE.getText().toString())) {
            emailE.setError("REQUIRED");
            return false;
        }
        if (!emailE.getText().toString().contains("@")) {
            emailE.setError("Bad Formated Email");
            return false;
        }
        if (TextUtils.isEmpty(passwordE.getText().toString())) {
            passwordE.setError("REQUIRED");
            return false;
        }
        if (passwordE.getText().length() < 8) {
            passwordE.setError("Must 8 Char");
            return false;
        } else {
            email = emailE.getText().toString();
            password = passwordE.getText().toString();
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

    private void forgetPaswword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText edittext = new EditText(getContext());
        builder.setMessage("Forget Password No Worries.");
        builder.setTitle("Enter Your Mail And Get The link to verify");
        builder.setView(edittext);
        builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String mail = edittext.getText().toString();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 100;   //x position
        wmlp.y = 100;   //y position
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                if (getFormData()) {
                    setSignin();
                }

                break;
            case R.id.go_to_singup:
                slidleft(new Register());
                break;
            case R.id.forgot:
                forgetPaswword();
                break;
        }
    }

    private void setSignin() {
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Processing");
        dialog.setMessage("Please wait while we are athenticating..");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        updateUi(user);
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Please Verify Your Email...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    void updateUi(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(getContext(), DashBoard.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.isEmailVerified()) {
            updateUi(user);
        }
    }
}
