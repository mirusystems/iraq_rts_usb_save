package com.mirusystems.usbsave;



import static com.mirusystems.usbsave.Manager.KEY_ED_CODE;
import static com.mirusystems.usbsave.Manager.KEY_GOV_CODE;
import static com.mirusystems.usbsave.Manager.KEY_PC_CODE;
import static com.mirusystems.usbsave.Manager.KEY_VRC_CODE;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;


import com.mirusystems.usbsave.data.UsbListEntity;
import com.mirusystems.usbsave.databinding.UsbListFragmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsbSaveFragment extends BaseFragment {
    private static final String TAG = "PsListFragment";
    private UsbListFragmentBinding binding;
    private UsbSaveViewModel mViewModel;
    private UsbAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int govCode;
    private int edCode;
    private int vrcCode;
    private int totalPollingStationCount;

    public static UsbSaveFragment newInstance() {
        return new UsbSaveFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(KEY_GOV_CODE)) {
                govCode = args.getInt(KEY_GOV_CODE);
            }
            if (args.containsKey(KEY_ED_CODE)) {
                edCode = args.getInt(KEY_ED_CODE);
            }
            if (args.containsKey(KEY_VRC_CODE)) {
                vrcCode = args.getInt(KEY_VRC_CODE);
            }
        }
        Log.v(TAG, "onCreateView: govCode = " + govCode + ", edCode = " + edCode + ", vrcCode = " + vrcCode + ", pcCode = ");
        binding = DataBindingUtil.inflate(inflater, R.layout.usb_list_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UsbSaveViewModel.class);
        LifecycleOwner owner = getViewLifecycleOwner();
        if (vrcCode > 0) {
            mViewModel.setCode(govCode, vrcCode);
        } else {
            mViewModel.setCode(govCode);
        }
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        adapter = new UsbAdapter(mViewModel);
        layoutManager = new LinearLayoutManager(requireContext());
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setAdapter(adapter);

        binding.layoutStop.setOnTouchListener((v, event) -> true);


        mViewModel.getPollingStationList().observe(owner, list -> {
            adapter.setList(list);
            totalPollingStationCount = list.size();
        });
        mViewModel.getSelectedPollingStation().observe(owner, event -> {
            UsbListEntity usbListEntity = event;
            if (usbListEntity != null) {
                Log.v(TAG, "getSelectedPollingStation: usbListEntity = " + usbListEntity);

            }
        });
        mViewModel.getPsWritingPosition().observe(owner, event -> {
            Integer position = event;
            if (position != null) {
                Log.v(TAG, "onActivityCreated: getPsWritingPosition = " + position);
                scroll(position);
                adapter.notifyItemChanged(position, UsbAdapter.STATE_READY);
            }
        });
        mViewModel.getUsbState().observe(owner, event -> {
            UsbState psState = event;
            if (psState != null) {
                Log.v(TAG, "onActivityCreated: getPsState = " + psState);
                int position = psState.getPosition();
                int state = psState.getState();
                if (state == UsbState.STATE_WRITE_SUCCESS) {
                    adapter.notifyItemChanged(position, UsbAdapter.STATE_SUCCESS);
                } else if (state == UsbState.STATE_WRITE_FAILURE) {
                    adapter.notifyItemChanged(position, UsbAdapter.STATE_FAILURE);
                } else if (state == UsbState.STATE_SELECTED) {
                    adapter.notifyItemChanged(position, UsbAdapter.STATE_SELECTED);
                } else if (state == UsbState.STATE_DESELECTED) {
                    adapter.notifyItemChanged(position, UsbAdapter.STATE_DESELECTED);
                }
            }
        });
        mViewModel.isWorking().observe(owner, event -> {
            Boolean isWorking = event;
            if (isWorking != null) {
                if (isWorking) {
                    binding.layoutStop.setVisibility(View.VISIBLE);
                    ((MainActivity) requireActivity()).setDisplayHomeAsUpEnabled(false);
                } else {
                    binding.layoutStop.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).setDisplayHomeAsUpEnabled(true);
                }
            }
        });
        mViewModel.getWorkerState().observe(owner, event -> {
            UsbSaveViewModel.WorkerState state = event;
            if (state != null) {
                switch (state) {
                    case WAITING_USB_CONNECTED: {
                        binding.statusText.setText("start USB COPY");
                        break;
                    }
                    case USB_CONNECTED : {
                        binding.statusText.setText("Waitting...");
                        break;
                    }
                    case WRITE_DONE: {
                        binding.statusText.setText("SUCCESS");
                        break;
                    }
                    case WRITE_FAIL: {
                        binding.statusText.setText("FAILURE");
                        break;
                    }
                    case WAITING_USB_DISCONNECTED: {
                        binding.statusText.setText("Insert USB");
                        break;
                    }
                }

                if (state == UsbSaveViewModel.WorkerState.WRITE_DONE) {
                    binding.layoutStop.setBackgroundColor(Color.parseColor("#4D00FF00"));
                } else if (state == UsbSaveViewModel.WorkerState.WRITE_FAIL) {
                    binding.layoutStop.setBackgroundColor(Color.parseColor("#4DFF0000"));
                } else {
                    binding.layoutStop.setBackgroundColor(Color.parseColor("#4D000000"));
                }
            }
        });
        mViewModel.getCompletedPollingStationCount().observe(owner, event -> {
            Integer completedPollingStationCount = event.getContentIfNotHandled();
            if (completedPollingStationCount != null) {
                requireActivity().setTitle(String.format(Locale.ENGLISH, "PS List(GOV: %d ( %d / %d ))", govCode, completedPollingStationCount, totalPollingStationCount));
            }
        });
        mViewModel.isStartButtonEnabled().observe(owner, event -> {
            Boolean isStartButtonEnabled = event;
            if (isStartButtonEnabled != null) {
                binding.startButton.setEnabled(isStartButtonEnabled);
            }
        });
        mViewModel.getAlreadyWrittenPso().observe(owner, stringEvent -> {
            String alreadyWrittenPso = stringEvent;
            if (alreadyWrittenPso != null) {
                showDialog(alreadyWrittenPso);
            }
        });
        mViewModel.getDismissDialog().observe(owner, event -> {
            Boolean dismissDialog = event;
            if (dismissDialog != null) {
                if (dismissDialog) {
                    BaseDialog.dismissDialog();
                }
            }
        });
        mViewModel.getToast().observe(owner, stringEvent -> {
            String toastMessage = stringEvent.getContentIfNotHandled();
            if (toastMessage != null) {
                showToast(toastMessage);
            }
        });
        mViewModel.getIsLoading().observe(owner, isLoading -> {
            if (isLoading) {
                BaseDialog.showLoadingDialog(requireActivity());
            } else {
                BaseDialog.loaddismissDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.onStopButtonClicked();
    }

    private void scroll(int position) {
        LinearSmoothScroller scroller = new LinearSmoothScroller(requireContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        scroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(scroller);
//        layoutManager.scrollToPosition(position);
//        new Handler().postDelayed(() -> {
//            layoutManager.scrollToPosition(position);
//        }, 200);
    }



//    public void makertsdata(boolean isChecked) {
//        mViewModel.setLoadingState(true);
//
//        // 비동기 작업을 처리하는 백그라운드 스레드 시작
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (isChecked) {
//                    // 데이터 설정 작업 수행
//                     mViewModel.setRtsData(pslist);
//
//                    // 작업이 완료되면 UI 스레드에서 로딩 화면을 숨깁니다.
//                    requireActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mViewModel.setLoadingState(false);
//                        }
//                    });
//                } else {
//                    // 다른 처리
//                }
//            }
//        }).start();
//    }


    private void showDialog(String psoInfo) {
        BaseDialog.showCustomDialog(requireActivity(), "PSO OVERRIDE", psoInfo, "Ok", "Cancel", new OnDialogClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                Log.v(TAG, "onPositiveButtonClicked: ");
                mViewModel.setPsoOverrideConfirm();
            }

            @Override
            public void onNegativeButtonClicked() {
                Log.v(TAG, "onNegativeButtonClicked: ");
            }
        });
    }


}