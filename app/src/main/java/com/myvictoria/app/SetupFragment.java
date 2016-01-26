package com.myvictoria.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mayur on 24/01/2016.
 */
public class SetupFragment extends Fragment {

    public static SetupFragment newInstance(int screen_number){
        Bundle bun = new Bundle();
        bun.putInt("screen_number", screen_number);
        SetupFragment frag = new SetupFragment();
        frag.setArguments(bun);
        return frag;
    }

    private Button next_button;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int screen = getArguments().getInt("screen_number");
        View rootView;
        switch (screen){
            case 1:
                rootView = inflater.inflate(R.layout.fragment_setup_userpass, container, false);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_setup_navigation,container, false);
                CircleImageView circ = (CircleImageView) rootView.findViewById(R.id.circleView);
                circ.setImageResource(R.drawable.setup_navigation1);
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_setup_userpass, container, false);
                break;
        }
        initialise(rootView);
        next_button.setOnClickListener((View.OnClickListener) getActivity());
        return rootView;
    }

    private void initialise(View rootView) {
        next_button = (Button) rootView.findViewById(R.id.next_button);
    }

}
