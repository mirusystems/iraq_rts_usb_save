package com.mirusystems.usbsave;

import static com.mirusystems.usbsave.App.LOG_SRCFILE_NAME;
import static com.mirusystems.usbsave.App.QR_SRCFILE_NAME;
import static com.mirusystems.usbsave.App.local_PATH;
import static com.mirusystems.usbsave.App.replacexml;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.io.Files;
import com.mirusystems.devices.printer.sato.PrinterManager;
import com.mirusystems.usbsave.data.AppDatabase;
import com.mirusystems.usbsave.data.LogDatabase;
import com.mirusystems.usbsave.data.UsbListEntity;
import com.mirusystems.usbsave.label.UsbLabel;
import com.mirusystems.usbsave.security.Crypto;
import com.mirusystems.usbsave.utility.SevenZip;
import com.mirusystems.usbsave.utility.ZipUtil;
import com.mirusystems.utility.ByteUtil;
import com.mirusystems.utility.MiruUtility;
import com.mirusystems.utils.image.BmpUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingDeque;

public class UsbSaveViewModel extends ViewModel implements OnItemSelectListener<UsbListEntity> {
    private static final String TAG = "UsbSaveViewModel";

    private AppDatabase db;
    private LogDatabase log;
    private int govCode;
    private int edCode;
    private int vrcCode;
    private int pcCode;
    public String mDeviceNo = "854342";
    public MiruUtility mUtil = new MiruUtility();
    public String mPrecinctID = "";
    public int mUSBSearchCnt = 0;
    public static String[] USB_PATH = new String[4];
    public static String[] USB_DISK = new String[4];
    private MutableLiveData<List<UsbListEntity>> pollingStationListLiveData = new MutableLiveData<>();
    private MutableLiveData<UsbListEntity> selectedPollingStationLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> psWritingPositionLiveData = new MutableLiveData<>();
    private MutableLiveData<UsbState> UsbStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isWorkingLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> completedPollingStationCountLiveData = new MutableLiveData<>();
    private MutableLiveData<WorkerState> workerStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isStartButtonEnabledLiveData = new MutableLiveData<>();
    private MutableLiveData<String> alreadyWrittenPsoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> dismissDialogLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<String>> toastLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private int startIndex = -1;
    private List<UsbListEntity> psList;
    private UsbState previousState = null;

    private WorkerCallback callback = new WorkerCallback() {

        @Override
        public void onStateChanged(WorkerState state) {
            Log.v(TAG, "onStateChanged: state = " + state);
            workerStateLiveData.postValue(state);
        }

        @Override
        public void onSuccess(int index) {
            Log.v(TAG, "onSuccess: index = " + index);
            UsbStateLiveData.postValue(new UsbState(index, UsbState.STATE_WRITE_SUCCESS));
            if (index >= psList.size() - 1) {
                isStartButtonEnabledLiveData.postValue(false);
            }
        }

        @Override
        public void onFailure(int index) {
            Log.v(TAG, "onFailure: index = " + index);
            UsbStateLiveData.postValue(new UsbState(index, UsbState.STATE_WRITE_FAILURE));
        }

        @Override
        public void onDone() {
            Log.v(TAG, "onDone: ");
            onStopButtonClicked();
        }

        @Override
        public void onCancelled() {
            Log.v(TAG, "onCancelled: ");
        }
    };

    public UsbSaveViewModel() {
        super();
        db = Manager.getDb();
        log = Manager.getLog();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void setCode(int govCode) {
        this.govCode = govCode;
        psList = db.usbSaveDao().getPsList(govCode);
        boolean isStartIndexSet = false;
        int index = 0;
        for (UsbListEntity ps : psList) {
            Log.v(TAG, "setCode: ps = " + ps);
            if (!isStartIndexSet) {
                if (Manager.getMode() == Manager.MODE_4_SIMULATION_USB && ps.done < 1) {
                    Log.v(TAG, "setCode: set startIndex = " + index);
                    startIndex = index;
                    isStartIndexSet = true;
                }
            }
            index++;
        }
        Log.v(TAG, "setCode: index = " + index + ", startIndex = " + startIndex);
        if (index == psList.size() && startIndex < 0) {
            isStartButtonEnabledLiveData.postValue(false);
        } else {
            psWritingPositionLiveData.postValue(startIndex);
        }
        pollingStationListLiveData.postValue(psList);
        int completed = 0;
        completed = db.usbSaveDao().getCompletedPsCountForSimulation(govCode);

        completedPollingStationCountLiveData.postValue(completed);
    }

//    public void setCode(int govCode, int edCode, int vrcCode, int pcCode) {
//        this.govCode = govCode;
//        this.edCode = edCode;
//        this.vrcCode = vrcCode;
//        this.pcCode = pcCode;
//        // TODO: 2021-05-28 DB가 PS 별로 정보를 가지고 있어야 한다
//        psList = db.pollingStationDao().getPsList(govCode, edCode, vrcCode, pcCode);
//        boolean isStartIndexSet = false;
//        int index = 0;
//        for (PollingStation ps : psList) {
//            if (!isStartIndexSet && ps.pso < 1) {
//                startIndex = index;
//                isStartIndexSet = true;
//            }
//            index++;
//        }
//        pollingStationListLiveData.postValue(psList);
//        int completed = 0;
//        if (Manager.getMode() == Manager.MODE_ELECTION_PSO_VVD) {
//            completed = db.pollingStationDao().getCompletedPsCountForElection(govCode, edCode);
//        } else {
//            completed = db.pollingStationDao().getCompletedPsCountForSimulation(govCode, edCode);
//        }
//        completedPollingStationCountLiveData.postValue(new Event<>(completed));
//    }

    public LiveData<List<UsbListEntity>> getPollingStationList() {
        return pollingStationListLiveData;
    }

    public LiveData<UsbListEntity> getSelectedPollingStation() {
        return selectedPollingStationLiveData;
    }

    public LiveData<Integer> getPsWritingPosition() {
        return psWritingPositionLiveData;
    }

    public LiveData<UsbState> getUsbState() {
        return UsbStateLiveData;
    }

    public LiveData<Boolean> isWorking() {
        return isWorkingLiveData;
    }

    public LiveData<WorkerState> getWorkerState() {
        return workerStateLiveData;
    }

    public LiveData<Integer> getCompletedPollingStationCount() {
        return completedPollingStationCountLiveData;
    }

    public LiveData<Boolean> isStartButtonEnabled() {
        return isStartButtonEnabledLiveData;
    }

    public LiveData<String> getAlreadyWrittenPso() {
        return alreadyWrittenPsoLiveData;
    }

    public LiveData<Boolean> getDismissDialog() {
        return dismissDialogLiveData;
    }
    public LiveData<Event<String>> getToast() {
        return toastLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void repaclexml(String xmlFilePath,String govNameReplacement,String vrcNameReplacement,String pcNameReplacement,String precinctIdReplacement){

        String outputFilePath = replacexml + "/" + precinctIdReplacement;


        try {
            // XML 파일 읽기
            BufferedReader reader = new BufferedReader(new FileReader(xmlFilePath));
            StringBuilder xmlContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // 각 요소별로 줄바꿈 추가
                if (line.startsWith("<") && line.endsWith(">")) {
                    xmlContent.append("\n"); // 이전 요소와 현재 요소를 구분하는 줄바꿈 추가
                }
                xmlContent.append(line).append("\n"); // 현재 라인 추가
            }
            reader.close();

            // 각 항목을 원하는 값으로 치환
            String replacedXmlContent = xmlContent.toString()
                    .replace("$$GOV_NAME$$", govNameReplacement)
                    .replace("$$VRC_NAME$$", vrcNameReplacement)
                    .replace("$$PC_NAME$$", pcNameReplacement)
                    .replace("$$precinct_id$$", precinctIdReplacement);

            // 새로운 XML 파일로 저장
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write(replacedXmlContent);
            writer.close();

            System.out.println("XML 파일이 성공적으로 치환되었고 새로운 파일로 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRtsData(UsbListEntity selectedPs) {
        Log.d(TAG, "setRtsData: pslist size %d" + psList.size());
        String txmlFileName;
        if (selectedPs != null) {
            String basePrecinctID = String.valueOf(selectedPs.getPcId()); // "100101"
            int govcode = selectedPs.gov_id;

            for (int i = 1; i <= selectedPs.getNumPs(); i++) {
                String precinctSuffix = String.format("%02d", i);
                mPrecinctID = basePrecinctID + precinctSuffix;
                if(selectedPs.elec_type == 50 || selectedPs.elec_type == 80 ){
                     txmlFileName = 50 + ".txml";
                }else {
                     txmlFileName = govcode + ".txml";
                }

                String txmlFilePath = "/mnt/sdcard/xml/" + txmlFileName;
                if (fileExists(txmlFilePath)) {
                    String govNameReplacement = selectedPs.gov_name;
                    String vrcNameReplacement = selectedPs.vrc_name;
                    String pcNameReplacement = selectedPs.pc_name;
                    String precinctIdReplacement = mPrecinctID + "_" + mDeviceNo;
                    repaclexml(txmlFilePath, govNameReplacement, vrcNameReplacement, pcNameReplacement, precinctIdReplacement);
                    SaveUSBTransformDataFile(replacexml, precinctIdReplacement, local_PATH, mPrecinctID + "_" + mDeviceNo, ".xml" , mPrecinctID);
                    SaveUSBTransformDataFile(replacexml ,precinctIdReplacement, local_PATH,mPrecinctID + "_" + mDeviceNo,".csv" , mPrecinctID);
                    SaveUSBTransformDataFile(replacexml ,precinctIdReplacement, local_PATH,mPrecinctID +"_" + mDeviceNo,".poi" , mPrecinctID);
                    SaveVotingResultToUsb();
                }
            }
            toastLiveData.postValue(new Event<>("WRITE SUCCESS"));
        }
    }


    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public boolean SaveUSBTransformDataFile(String srcpath, String srcname, String dstpath, String dstname, String dstext, String mPrecinctID) {
        try {
            byte[] src_data = MiruUtility.convertFileToByteArray(srcpath, srcname, null, 0);
            int src_len = src_data.length;
            byte[] slen_bytes = ByteUtil.toByteArray(src_len);
            String lenext = "";

            String hash_src = Crypto.SHA2_digestStrg(src_data);
            byte[] hashBytes = hash_src.getBytes();
            String hashext = "";

            if (dstext.endsWith("xml")) {
                lenext = ".len";
                hashext = ".hash";
            } else if (dstext.endsWith("poi")) {
                lenext = ".pen";
                hashext = ".poh";
            } else if (dstext.endsWith("log")) {
                lenext = ".gen";
                hashext = ".loh";
            } else if (dstext.endsWith("csv")) {
                lenext = ".cen";
                hashext = ".csh";
            } else {
                lenext = ".den";
                hashext = ".pdh";
            }
            Log.d(TAG, "convertSaveUSBTransmitDataFile: srcpath=" + srcpath + ", srcname=" + srcname + ", dstpath=" + dstpath + ", dstname=" + dstname + ", srcext=" + dstext + ", lenext=" + lenext + ", hashext=" + hashext);

            MiruUtility.convertByteArrayToFile(hash_src.getBytes(), hash_src.length(), dstpath + "/" + dstname + hashext);
            MiruUtility.convertByteArrayToFile(slen_bytes, slen_bytes.length, dstpath + "/" + dstname + lenext);

            if (!srcname.equals(dstname + dstext)) {
                MiruUtility.copyStream(new FileInputStream(new File(srcpath, srcname)), new File(srcpath, dstname + dstext));
//                mUtil.RemoveFile(srcpath+"/"+srcname);
            }
//            ZipUtil.zip(srcpath + File.separator + srcname + dstext, srcpath + File.separator + "_zipped_" + srcname + ".zip");
            SevenZip.zipFile(srcpath, dstname + dstext, "_zipped_" + srcname + ".7z");
            MiruUtility.MiruService("sync");
            Sleep(50);
          //  MiruUtility.RemoveFile(srcpath + "/" + dstname + dstext);

            byte[] plainText = MiruUtility.convertFileToByteArray(srcpath , "_zipped_" + srcname + ".7z", null, 0);
            byte[] cipherText = Crypto.AESEncryptHost(plainText, mPrecinctID, hashBytes);
            MiruUtility.RemoveFile(srcpath + "/" + "_zipped_" + srcname + ".7z");
            MiruUtility.MiruService("sync");
            Sleep(50);

            MiruUtility.convertByteArrayToFile(cipherText, cipherText.length, dstpath + "/" + dstname + dstext);

            MiruUtility.MiruService("sync");
        } catch (Exception e) {
            e.printStackTrace();
            toastLiveData.postValue(new Event<>("WRITE FAILURE"));
            return (false);
        }
        return (true);
    }

    public boolean SaveVotingResultToUsb() {
        try {
            String resName = mPrecinctID + "_" + mDeviceNo ;

//            SaveTransformDataToUSB(xmlSavePath);
            SaveTransformDataToUSB(local_PATH, resName, ".xml", ".hash", ".len");
            SaveTransformDataToUSB(local_PATH, resName, ".poi", ".poh", ".pen");
            SaveTransformDataToUSB(local_PATH, resName, ".csv", ".csh", ".cen");
        } catch (Exception ex) {
            toastLiveData.postValue(new Event<>("WRITE FAILURE"));
            return (false);
        }
        return (true);
    }

    public boolean SaveTransformDataToUSB(String srcpath, String srcname, String srcext, String hashext, String lenext) {
        try {
            byte[] srclen = MiruUtility.convertFileToByteArray(srcpath, srcname + lenext, null, 0);
            byte[] hashBytes = MiruUtility.convertFileToByteArray(srcpath, srcname + hashext, null, 0);
            byte[] cipherText = MiruUtility.convertFileToByteArray(srcpath, srcname + srcext, null, 0);

            if (srclen == null || hashBytes == null || cipherText == null)
                return (false);

            int enc_len = cipherText.length;
            int cipher_crc = MiruUtility.CalculateCRC16Data(cipherText, enc_len);
            byte[] crcbyte = ByteUtil.toByteArray(cipher_crc);

            SearchUSBDisk();
            for (int i = 0; i < mUSBSearchCnt; ++i) {
                File f = new File(USB_PATH[i] + "/RTS");
                if (f.exists() == false)
                    f.mkdirs();

                MiruUtility.convertByteArrayToFile(crcbyte, srclen, cipherText, enc_len, mPrecinctID, mDeviceNo, USB_PATH[i] + "/RTS/" + srcname + srcext);
                MiruUtility.convertByteArrayToFile(hashBytes, hashBytes.length, USB_PATH[i] + "/RTS/" + srcname + hashext);
            }
            MiruUtility.MiruService("sync");
            Sleep(1000);
            return (true);
        } catch (Exception e) {
            toastLiveData.postValue(new Event<>("WRITE FAILURE"));
            e.printStackTrace();
        }
        return (false);
    }

    public synchronized void SearchUSBDisk() {
        mUSBSearchCnt = 0;
        for (int i = 0; i < USB_DISK.length; ++i) {
//            Log.i("USB", "finding usb path : [" + USB_DISK[i] + ConfigManager.COMMON_DIR + "][" + mUSBSearchCnt + "]");
            if (new File(USB_DISK[i]).exists()) {
                if (mUSBSearchCnt < USB_PATH.length) {
                    USB_PATH[mUSBSearchCnt] = USB_DISK[i];
//                    Log.i("USB", "exists usb path : [" + USB_PATH[mUSBSearchCnt] + "][" + mUSBSearchCnt + "]");
                    mUSBSearchCnt++;
                }
                if (mUSBSearchCnt >= USB_PATH.length)
                    break;
            }
        }
//        Log.i("USB","found usb count : "+mUSBSearchCnt);
    }


    public void Sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            ;
        }  // 500 ms sleep.
    }


    @Override
    public void onItemSelected(UsbListEntity usbListEntity) {
        Log.v(TAG, "onItemSelected: usbListEntity = " + usbListEntity);
        selectedPollingStationLiveData.postValue(usbListEntity);
     //   setRtsData(usbListEntity);
        // 특정 PS를 선택하면 선택한 PS부터 쓰기 시작해라
        startIndex = pollingStationListLiveData.getValue().indexOf(usbListEntity);
        isStartButtonEnabledLiveData.postValue(true);

        if (previousState != null) {
            if (previousState.getState() == UsbState.STATE_SELECTED) {
                previousState.setState(UsbState.STATE_DESELECTED);
                Log.d(TAG, "onItemSelected: previousState = " + previousState);
                UsbStateLiveData.setValue(previousState);
            }
        }
        UsbState state = new UsbState(startIndex, UsbState.STATE_SELECTED);
        UsbStateLiveData.postValue(state);
        previousState = state;
    }

    public void onStartButtonClicked() {
        Log.v(TAG, "onStartButtonClicked: ");
        isWorkingLiveData.postValue(true);
        startWorker();
    }

    public void onStopButtonClicked() {
        Log.v(TAG, "onStopButtonClicked: ");

        stopWorker();
        isWorkingLiveData.postValue(false);
    }

    public void setPsoOverrideConfirm() {
        if (workerThread != null) {
            workerThread.setOverrideConfirm();
        }
    }

    private void startWorker() {
        if (workerThread != null) {
            workerThread.stopWork();
            workerThread.interrupt();
            workerThread = null;
        }
        if (workerThread == null) {
            workerThread = new WorkerThread(callback);
            workerThread.start();
        }
    }

    private void stopWorker() {
        if (workerThread != null) {
            workerThread.stopWork();
            workerThread.interrupt();
            workerThread = null;
        }
    }


    enum WorkerState {
        UNKNOWN,
        WORKER_START,
        WAITING_USB_CONNECTED,
        USB_CONNECTED,
        WRITING_USB,
        WRITE_DONE,
        WRITE_FAIL,
        WAITING_USB_DISCONNECTED,
        USB_DISCONNECTED,
        WORKER_STOP
    }

    interface WorkerCallback {
        void onStateChanged(WorkerState state);
        void onSuccess(int index);
        void onFailure(int index);
        void onDone();
        void onCancelled();
    }

    private boolean isWorking = false;
    public static WorkerThread workerThread = null;

    class WorkerThread extends Thread {
        private WorkerCallback callback;
        private WorkerState state = WorkerState.UNKNOWN;
        private List<UsbListEntity> psList;
        private int psCount;
        private int psIndex;
        private UsbListEntity ps;
        private boolean shouldOverride = false;
        private boolean hasWrittenPso = false;

        private PrinterManager printerManager = PrinterManager.getInstance();

        public WorkerThread(WorkerCallback callback) {
            this.callback = callback;
            psList = pollingStationListLiveData.getValue();
            psCount = psList.size();
            psIndex = startIndex;
            Log.v(TAG, "WorkerThread: psIndex = " + psIndex);
        }

        @Override
        public void run() {
            isWorking = true;
            setState(WorkerState.WAITING_USB_CONNECTED);
            while (isWorking) {
                if (shouldOverride) {
                    setState(WorkerState.WRITING_USB);
                    shouldOverride = false;
                }
            }

            if (psIndex < psCount) {
                callback.onCancelled();
            } else {
                callback.onDone();
            }

            Log.v(TAG, "run: STOPPED!!");
        }

        private void setState(WorkerState state) {
            if (this.state != state) {
                this.state = state;
                notifyStateChanged(state);
                work(state);
            }
        }

        private void notifyStateChanged(WorkerState state) {
            callback.onStateChanged(state);
        }

        private void work(WorkerState state) {
            Log.v(TAG, "work: state = " + state);
            switch (state) {
                case WAITING_USB_CONNECTED: {
                    Log.v(TAG, "work: getPs " + psIndex);
                    ps = psList.get(psIndex);
                    previousState = new UsbState(psIndex, UsbState.STATE_READY);
                    psWritingPositionLiveData.postValue(psIndex);
                    for (int i = 0; i < USB_DISK.length; ++i) {
                        USB_DISK[i] = String.format(Locale.US, "/mnt/usbdisk%d", i + 1);
                    }
                    SearchUSBDisk();
                    if (mUSBSearchCnt > 0) {
                        setState(WorkerState.USB_CONNECTED);
                    }
                    break;
                }
                case USB_CONNECTED: {
                    setRtsData(ps);
                    setState(WorkerState.WRITING_USB);
                    break;
                }
                case WRITING_USB: {
                    if (printUsbLabel(ps)) {
                        setState(WorkerState.WRITE_DONE);
                    } else {
                        setState(WorkerState.WRITE_FAIL);
                    }
                    hasWrittenPso = false;
                    break;
                }
                case WRITE_DONE: {
                    ps.done = 1;
                    db.usbSaveDao().update(ps);
                    int completed = 0;
                    completed = db.usbSaveDao().getCompletedPsCountForSimulation(govCode);

                    completedPollingStationCountLiveData.postValue(completed);
                    callback.onSuccess(psIndex);
                    previousState.setState(UsbState.STATE_WRITE_SUCCESS);
                    if (!App.isEducationMode()) {
                        psIndex++;
                    }
                    break;
                }
                case WRITE_FAIL: {
                    callback.onFailure(psIndex);
                    previousState.setState(UsbState.STATE_WRITE_FAILURE);
                    setState(WorkerState.WAITING_USB_DISCONNECTED);
                    break;
                }
                case WAITING_USB_DISCONNECTED: {
                    break;
                }
                case USB_DISCONNECTED: {
                    if (psIndex >= psCount) {
                        isWorking = false;
                        setState(WorkerState.WORKER_STOP);
                    } else {
                        setState(WorkerState.WAITING_USB_DISCONNECTED);
                    }
                    if (hasWrittenPso) {
                        dismissDialogLiveData.postValue(true);
                        hasWrittenPso = false;
                    }
                    break;
                }
            }
        }

        public void stopWork() {
            isWorking = false;
            if (previousState.getState() == UsbState.STATE_READY) {
                previousState.setState(UsbState.STATE_DESELECTED);
                UsbStateLiveData.setValue(previousState);
            }
        }

        public void setOverrideConfirm() {
            Log.v(TAG, "setOverrideConfirm: ");
            shouldOverride = true;
        }


        private boolean printUsbLabel(UsbListEntity ps) {
            Bitmap labelBitmap;
            Log.v(TAG, "printPsoLabel: Manager.getMode() = " + Manager.getMode());
            UsbLabel label = new UsbLabel(ps);
            labelBitmap = label.draw();

            Log.v(TAG, "printPsoLabel: labelBitmap = " + labelBitmap.getWidth() + "x" + labelBitmap.getHeight());
            byte[] bmp = BmpUtil.toBmp(labelBitmap, 1);
            new Thread(() -> {
                try {
                    Files.write(bmp, new File("/sdcard/usbvvd.bmp"));
                    Log.v(TAG, "printPsoLabel: save image");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            if (!App.isPsoLabelPrint()) {
                return true;
            }

            return printerManager.printBmp(bmp);
        }
    }
}





