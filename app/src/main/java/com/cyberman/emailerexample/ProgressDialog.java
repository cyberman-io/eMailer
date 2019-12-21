package com.cyberman.emailerexample;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class ProgressDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sending eMail...");
        View v = requireActivity().getLayoutInflater().inflate(R.layout.progress_dialog_layout,null);
        builder.setView(v);
        return builder.create();
    }
}
