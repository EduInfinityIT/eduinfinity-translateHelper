package com.eduinfinity.dimu.translatehelper.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.utils.SrtParse;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;

/**
 * Created by Dimu on 11/20/14.
 */
public class PageFragment extends Fragment {
    private TextTrackImpl.Line line;
    private EditText trans;

    public static Fragment newInstance(TextTrackImpl.Line line) {
        PageFragment fragment = new PageFragment();
        fragment.setLine(line);
        return fragment;
    }
//
//    private PageFragment(TextTrackImpl.Line line) {
//        super();
//        this.line = line;
//    }
    private void  setLine(TextTrackImpl.Line line){
        this.line = line;
    }
//    public PageFragment() {
//        super();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
//            mContent = savedInstanceState.getString(KEY_CONTENT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpage_srt, null);
        TextView index = (TextView) view.findViewById(R.id.textView_index);
        TextView s = (TextView) view.findViewById(R.id.textView_startTime);
        TextView e = (TextView) view.findViewById(R.id.textView_endTime);
        TextView l = (TextView) view.findViewById(R.id.textView_lineString);
        trans = (EditText) view.findViewById(R.id.editText_trans);

        index.setText("" + line.index);
        s.setText("" + SrtParse.convertTime2String(line.startTime));
        e.setText("" + SrtParse.convertTime2String(line.endTime));
        l.setText("" + line.lineString);
        trans.setText("" + line.getTrans());
        trans.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String translatedString = trans.getText().toString().trim();
                line.trans = translatedString;
                return false;
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        Log.i("Fragment", "Fragment on pause");
        super.onPause();
    }
}