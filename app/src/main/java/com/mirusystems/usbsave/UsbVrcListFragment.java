package com.mirusystems.usbsave;


import static com.mirusystems.usbsave.Manager.KEY_ED_CODE;
import static com.mirusystems.usbsave.Manager.KEY_GOV_CODE;
import static com.mirusystems.usbsave.Manager.KEY_VRC_CODE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;


import com.mirusystems.usbsave.data.UsbDistrict;
import com.mirusystems.usbsave.databinding.UsbVrcListFragmentBinding;

import java.util.Collections;
import java.util.Locale;

public class UsbVrcListFragment extends BaseFragment {
    private static final String TAG = "VrcListFragment";

    private UsbVrcListFragmentBinding binding;
    private UsbVrcListViewModel mViewModel;
    private UsbDistrictAdapter adapter;

    private int govCode;

    public static UsbVrcListFragment newInstance() {
        return new UsbVrcListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(KEY_GOV_CODE)) {
                govCode = args.getInt(KEY_GOV_CODE);
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.usb_vrc_list_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UsbVrcListViewModel.class);
        mViewModel.setCode(govCode);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        adapter = new UsbDistrictAdapter(mViewModel);
        binding.recycler.setLayoutManager(new GridLayoutManager(requireContext(), 6));
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setAdapter(adapter);

        mViewModel.getDistrictList().observe(getViewLifecycleOwner(), list -> {
            Collections.sort(list, (o1, o2) -> o1.getCode() - o2.getCode());
            adapter.setList(list);
        });
        mViewModel.getSelectedDistrict().observe(getViewLifecycleOwner(), districtEvent -> {
            UsbDistrict district = districtEvent.getContentIfNotHandled();
            if (district != null) {
                Log.v(TAG, "getSelectedDistrict: district = " + district);
                Bundle args = new Bundle();
                args.putInt(KEY_GOV_CODE, govCode);
                args.putInt(KEY_ED_CODE, govCode);
                args.putInt(KEY_VRC_CODE, district.getCode());
                navController.navigate(R.id.usbsaveListFragment, args);
            }
        });

        requireActivity().setTitle(String.format(Locale.ENGLISH, "VRC List(GOV: %d, ED: %d)", govCode, govCode));
    }

}