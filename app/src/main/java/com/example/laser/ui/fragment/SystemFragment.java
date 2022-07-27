package com.example.laser.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.BrightnessUtils;
import com.example.laser.R;
import com.example.laser.api.api;
import com.example.laser.ui.app.MyApplication;
import com.vondear.rxtool.RxSPTool;
import com.vondear.rxtool.view.RxToast;

import java.text.DecimalFormat;

/**
 * Created by  on 2021/5/4.
 */

public class SystemFragment extends Fragment {
    private Spinner TargetReportSpinner;
    private String TargetRecordType;
    private Switch GunVoiceSwitch;
    private boolean GunVoiceIsTrue;
    private Spinner TargetMianSpinner;
    private Spinner ShotNumSpinner;
    private Switch AutoPrintSwitch;
    private Switch ShotVoiceSwitch;
    private boolean ShotVoiceIsTrue;

    private OnListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system, container, false);
        CreatTargetMianSpinner(view);
        CreatTargetReportSpinner(view);
        CreatShotNumSpinner(view);
        CreatGunVoiceSwitch(view);
        CreatShotVoiceSwitch(view);
        CreatAutoPrintSwitch(view);
        setLight(view);
        setIp(view);
        return view;
    }

    public void setListener(OnListener mListener) {
        this.mListener = mListener;
    }

    private void CreatTargetMianSpinner(View view) {
        TargetMianSpinner = view.findViewById(R.id.fragment_system_target_mian_spinner);
        TargetMianSpinner.setSelection(RxSPTool.getInt(this.getContext(), "SelectTargetType"));
        TargetMianSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RxSPTool.putInt(SystemFragment.this.getContext(), "SelectTargetType", TargetMianSpinner.getSelectedItemPosition());
                mListener.SelectTargetMian(SystemFragment.this, TargetMianSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void CreatTargetReportSpinner(View view) {
        TargetReportSpinner = view.findViewById(R.id.fragment_system_type_spinner);
        TargetRecordType = RxSPTool.getString(this.getContext(), "TargetRecordType");
        String[] TargetRecordTypestrs = getResources().getStringArray(R.array.TargetRecordTpye);
        for (int i = 0; i < TargetRecordTypestrs.length; i++) {
            if (TargetRecordTypestrs[i].equals(TargetRecordType)) {
                TargetReportSpinner.setSelection(i);
            }
        }
        TargetReportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!TargetRecordType.equals(TargetReportSpinner.getSelectedItem().toString())) {
                    TargetRecordType = TargetReportSpinner.getSelectedItem().toString();
                    RxSPTool.putString(SystemFragment.this.getContext(), "TargetRecordType", TargetReportSpinner.getSelectedItem().toString());
                    mListener.SelectTargetRecordType(SystemFragment.this, TargetRecordType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void CreatShotNumSpinner(View view) {
        ShotNumSpinner = view.findViewById(R.id.fragment_system_shot_num_spinner);
        String[] shotnums = getResources().getStringArray(R.array.NumberOfBullets);
        int num = RxSPTool.getInt(this.getContext(), "NumberOfBullets");
        for (int i = 0; i < shotnums.length; i++) {
            if (Integer.parseInt(shotnums[i]) == num) {
                ShotNumSpinner.setSelection(i);
            }
        }
        ShotNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RxSPTool.putInt(SystemFragment.this.getContext(), "NumberOfBullets", Integer.parseInt(ShotNumSpinner.getSelectedItem().toString()));
                mListener.SelectShotNum(SystemFragment.this, Integer.parseInt(ShotNumSpinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void CreatGunVoiceSwitch(View view) {
        GunVoiceSwitch = view.findViewById(R.id.fragment_system_gun_voice_switch);
        GunVoiceIsTrue = RxSPTool.getBoolean(this.getContext(), "GunVoice");
        if (GunVoiceIsTrue) {
            GunVoiceSwitch.setChecked(true);
        } else {
            GunVoiceSwitch.setChecked(false);
        }
        GunVoiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GunVoiceIsTrue = GunVoiceSwitch.isChecked();
                RxSPTool.putBoolean(SystemFragment.this.getContext(), "GunVoice", GunVoiceIsTrue);
                mListener.SelectGunVoice(SystemFragment.this, GunVoiceIsTrue);
            }
        });
    }

    private void CreatShotVoiceSwitch(View view) {
        ShotVoiceSwitch = view.findViewById(R.id.fragment_system_shot_voice_switch);
        ShotVoiceIsTrue = RxSPTool.getBoolean(this.getContext(), "ShotVoice");
        if (ShotVoiceIsTrue) {
            ShotVoiceSwitch.setChecked(true);
        } else {
            ShotVoiceSwitch.setChecked(false);
        }
        ShotVoiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShotVoiceIsTrue = ShotVoiceSwitch.isChecked();
                RxSPTool.putBoolean(SystemFragment.this.getContext(), "ShotVoice", ShotVoiceIsTrue);
                mListener.SelectShotVoice(SystemFragment.this, ShotVoiceIsTrue);
            }
        });
    }

    private void CreatAutoPrintSwitch(View view) {
        AutoPrintSwitch = view.findViewById(R.id.fragment_system_auto_print_switch);
        if (RxSPTool.getBoolean(this.getContext(), "AutoPrint")) {
            AutoPrintSwitch.setChecked(true);
        } else {
            AutoPrintSwitch.setChecked(false);
        }
        AutoPrintSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxSPTool.putBoolean(SystemFragment.this.getContext(), "AutoPrint", AutoPrintSwitch.isChecked());
                mListener.SelectAutoPrint(SystemFragment.this, AutoPrintSwitch.isChecked());
            }
        });
    }

    private void setLight(View view) {
        SeekBar seekBar = view.findViewById(R.id.main_light);
        int brightness = BrightnessUtils.getBrightness();
        seekBar.setProgress(brightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BrightnessUtils.setWindowBrightness(getActivity().getWindow(), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void setIp(View view) {
        String ip = RxSPTool.getString(this.getContext(), api.ip);
        if (ip == null || ip.equals("")) {
            ip = "192.168.1.1";
        }
        int port = RxSPTool.getInt(this.getContext(), api.port);
        if (port <= 0) {
            port = 9291;
        }

        EditText mip = view.findViewById(R.id.main_ip);
        EditText mport = view.findViewById(R.id.main_port);
        TextView textView = view.findViewById(R.id.main_socket_con);

        mip.setText(ip);
        mport.setText(String.valueOf(port));

        textView.setOnClickListener(view1 -> {
            if (mip.getText().toString() != null || mport.getText().toString() != null) {
                RxSPTool.putString(MyApplication.getApplication(), api.ip,mip.getText().toString());
                RxSPTool.putInt(MyApplication.getApplication(), api.port, Integer.parseInt(mport.getText().toString()));
                RxToast.showToast("保存成功");
            } else {
                RxToast.showToast("服务器IP或端口号不能为空");
            }
        });
    }

    public String mFloat(int a, int b) {
        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数
        return df.format((float) a / b);
    }


    public interface OnListener {
        void SelectTargetMian(SystemFragment systemFragment, int position);

        void SelectTargetRecordType(SystemFragment systemFragment, String SelectedTargetRecordType);

        void SelectShotNum(SystemFragment systemFragment, int shotnums);

        void SelectGunVoice(SystemFragment systemFragment, boolean isTrue);

        void SelectShotVoice(SystemFragment systemFragment, boolean isTrue);

        void SelectAutoPrint(SystemFragment systemFragment, boolean isAuto);

    }
}
