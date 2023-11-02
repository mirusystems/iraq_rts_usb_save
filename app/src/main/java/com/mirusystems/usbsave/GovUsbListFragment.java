package com.mirusystems.usbsave;

import static com.mirusystems.usbsave.Manager.KEY_ED_CODE;
import static com.mirusystems.usbsave.Manager.KEY_GOV_CODE;

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
import com.mirusystems.usbsave.databinding.GovUsbListFragmentBinding;

import java.util.Collections;

public class GovUsbListFragment extends BaseFragment {
    private static final String TAG = "GovListFragment";

    private GovUsbListFragmentBinding binding;
    private GovUsbListViewModel mViewModel;
    private UsbDistrictAdapter adapter;

    public static GovUsbListFragment newInstance() {
        return new GovUsbListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.gov_usb_list_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GovUsbListViewModel.class);
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
                int govCode = district.getCode();
                args.putInt(KEY_GOV_CODE, govCode);
                args.putInt(KEY_ED_CODE, govCode);
                navController.navigate(R.id.usbsaveListFragment, args);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.updateList();
    }
}