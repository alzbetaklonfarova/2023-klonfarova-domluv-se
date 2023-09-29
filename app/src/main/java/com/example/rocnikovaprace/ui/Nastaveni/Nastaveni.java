package com.example.rocnikovaprace.ui.Nastaveni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.databinding.FragmentSlideshowBinding;
import com.example.rocnikovaprace.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Nastaveni#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Nastaveni extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    TextView mail;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    public Nastaveni() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NastaveniFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Nastaveni newInstance(String param1, String param2) {
        Nastaveni fragment = new Nastaveni();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        /*File file = new File(getContext().getFilesDir(), "cislo.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write("ano");
            bw.newLine();
            bw.flush();

        } catch (Exception e) {
            System.out.println("Do souboru se nepovedlo zapsat.");
        }*/


        LayoutInflater lf = getActivity().getLayoutInflater();
        View v =  lf.inflate(R.layout.fragment_nastaveni, container, false);
        mail = v.findViewById(R.id.editheslo);
        mAuth = FirebaseAuth.getInstance();

        mail.setText(mAuth.getCurrentUser().getEmail().toString());


        return v;
    }

}