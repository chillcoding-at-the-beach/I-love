package com.chillcoding.mycuteheart.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


import com.chillcoding.mycuteheart.R;

import java.util.Random;

/**
 * Created by macha on 13/04/2017.
 */

public class MyLikeDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String[] array = getResources().getStringArray(R.array.love_quote);

        final int length = array.length;

        final Random rand = new Random();

        builder.setMessage(array[rand.nextInt(length)]);

        builder.setPositiveButton(R.string.like, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }
}
