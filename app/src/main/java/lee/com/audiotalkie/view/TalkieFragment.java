package lee.com.audiotalkie.view;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lee.com.audiotalkie.R;
import lee.com.audiotalkie.TalkieApplication;
import lee.com.audiotalkie.model.NetConfig;
import lee.com.audiotalkie.presenter.LogAdapter;
import lee.com.audiotalkie.presenter.TalkiePresenter;
import lee.com.audiotalkie.utils.DataUtil;

public class TalkieFragment extends Fragment implements TalkieView {

    private OnFragmentInteractionListener mListener;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TalkiePresenter presenter;

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private List<String> logList = new ArrayList<>();
    private Button recordStart, recordStop, trackStart, trackStop, socketConnect, socketClose;
    private EditText ipEdit;
    private Button voiceBt;
    private TextView catchCountTv;


    public static TalkieFragment newInstance(String param1, String param2) {
        TalkieFragment fragment = new TalkieFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = rootView.findViewById(R.id.rv_log);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TalkieApplication.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        logAdapter = new LogAdapter(logList);
        recyclerView.setAdapter(logAdapter);
        recyclerView.setItemAnimator(null);

        recordStart = rootView.findViewById(R.id.bt_record_start);
        recordStop = rootView.findViewById(R.id.bt_record_stop);
        trackStart = rootView.findViewById(R.id.bt_track_start);
        trackStop = rootView.findViewById(R.id.bt_track_stop);
        socketConnect = rootView.findViewById(R.id.bt_tcp_connect);
        socketClose = rootView.findViewById(R.id.bt_tcp_close);
        ipEdit = rootView.findViewById(R.id.edit_ip);
        voiceBt = rootView.findViewById(R.id.bt_voice);
        catchCountTv = rootView.findViewById(R.id.count_catch);
        socketClose.setEnabled(false);
        recordStop.setEnabled(false);
        trackStop.setEnabled(false);
        recordStart.setOnClickListener((v) -> {
            recordStart.setEnabled(false);
            presenter.recordStart();
            recordStop.setEnabled(true);
        });
        recordStop.setOnClickListener((v) -> {
            recordStop.setEnabled(false);
            presenter.recordStop();
            recordStart.setEnabled(true);
        });
        trackStart.setOnClickListener((v) -> {
            trackStart.setEnabled(false);
            presenter.trackStart();
            trackStop.setEnabled(true);
        });
        trackStop.setOnClickListener((v) -> {
            trackStop.setEnabled(false);
            presenter.trackStop();
            trackStart.setEnabled(true);
        });
        socketConnect.setOnClickListener((v) -> {
            if (null != ipEdit.getText() && !ipEdit.getText().toString().trim().isEmpty()) {
                socketConnect.setEnabled(false);
                presenter.socketInit(ipEdit.getText().toString().trim(), Integer.parseInt(NetConfig.PORT.getValue()));
                socketClose.setEnabled(true);
            }
        });
        socketClose.setOnClickListener((v) -> {
            socketConnect.setEnabled(true);
            presenter.TcpClose();
            socketClose.setEnabled(false);
        });
        rootView.findViewById(R.id.bt_tcp_test).setOnClickListener((v) -> presenter.TcpTest());
        rootView.findViewById(R.id.bt_udp_test).setOnClickListener((v) -> presenter.UdpTest());
        voiceBt.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                System.out.println("bt_voice    ACTION_DOWN");
                presenter.recordStart();
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                System.out.println("bt_voice    ACTION_UP");
                presenter.recordStop();
            }
            return false;
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String ip = DataUtil.getLocalIpAddress();
        System.out.println("ip =  " + ip);

        ipEdit.setText(ip);

        presenter.socketInit(NetConfig.HOST.getValue(), Integer.parseInt(NetConfig.PORT.getValue()));

        presenter.UdpTest();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setPresenter(TalkiePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void logAdd(String msg) {
        logAdapter.addNewItem(msg);
        recyclerView.scrollToPosition(logAdapter.getItemCount() - 1);
    }

    @Override
    public void logClean() {
        logList.clear();
        logAdapter.updateData(logList);
    }

    @Override
    public void socketReset() {
        socketConnect.setEnabled(true);
        socketClose.setEnabled(false);
    }

    @Override
    public void setSpeak(boolean isSpeaking) {
        voiceBt.setSelected(isSpeaking);
        voiceBt.setEnabled(!isSpeaking);
    }

    @Override
    public void setCatchCount(long count) {
        catchCountTv.setText("" + count);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
