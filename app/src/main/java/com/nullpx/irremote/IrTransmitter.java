package com.nullpx.irremote;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.CONSUMER_IR_SERVICE;

/**
 * This class does the transmitting part. it loads the signals from the file and sends them to
 * the device
 * Created by sajjadg on 2/4/17.
 *
 *
 // transmit the pattern at 38.4KHz
 //        consumerIrManager.transmit(38400, pattern);
 */
public class IrTransmitter {

    private static final String TAG = IrTransmitter.class.getName();
    private final ConsumerIrManager consumerIrManager;
    private HashMap<String, IrSignal> irSignals;

    /**
     * Constructor gets the brand and model of the device and loads the configuration for that model
     * also it initializes the ir transmitter
     *
     * @param device
     * @param brand
     * @param context
     */
    public IrTransmitter(String device, String brand, Context context) {
        //keep ir manager
        consumerIrManager = (ConsumerIrManager) context.getSystemService(CONSUMER_IR_SERVICE);

        //TODO
//        irSignals = new HashMap<>();
        //read model signal file and and keep the signal somewhere to be used whenever we want
        //to send a signal
//        irSignals = hardcodedSignalLoading();
        irSignals = jsonSignalLoading(context, device, brand);
    }

    private HashMap<String, IrSignal> jsonSignalLoading(Context context, String device, String brand) {
        HashMap<String, IrSignal> map = new HashMap<>();

        Log.d(TAG, "loading " + device + " - " + brand + " json config file");
        //read json file and fill the map
        StringBuilder buf = new StringBuilder();
        InputStream json = null;
        try {
            json = context.getAssets().open(device.toLowerCase() + "/" + brand.toLowerCase() + ".json");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (json != null) {
            try {
                JSONObject jObject = new JSONObject(buf.toString());
                Iterator<String> keys = jObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jObject.getString(key);
//                    System.out.println(key + " " + value);
                    map.put(key, count2duration(value));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * Only for testing
     * remove for production
     *
     * @return
     */
    @Deprecated
    private HashMap<String, IrSignal> hardcodedSignalLoading() {

        HashMap<String, IrSignal> map = new HashMap<>();

        String okCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        IrSignal irSignal = count2duration(okCode);
        map.put(IR_SIGNAL.OK.name(), irSignal);

        //TODO:check this signal
        String muteBack = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.BACK.name(), count2duration(muteBack));

        String menuCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.MENU.name(), count2duration(menuCode));

        String exitCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.EXIT.name(), count2duration(exitCode));

        String muteCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.MUTE.name(), count2duration(muteCode));

        String powerToggleCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        String powerCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.POWER.name(), count2duration(powerCode));

        String lastCode = "0000 006D 0022 0002 0156 00AB 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0015 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0041 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0619 0156 0056 0015 0E6F";
        map.put(IR_SIGNAL.LAST.name(), count2duration(lastCode));

        String rightArrowVolumeUpCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.RIGHT_ARROW.name(), count2duration(rightArrowVolumeUpCode));

        String leftArrowVolumeDownCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.LEFT_ARROW.name(), count2duration(leftArrowVolumeDownCode));

        String upArrowNextChannelCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.UP_ARROW.name(), count2duration(upArrowNextChannelCode));

        //String leftCode = "0000 006D 0022 0002 0156 00AC 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0619 0156 0056 0015 0E6F";
//        String leftCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
//        String prevChannelCode = "0000 006D 0022 0002 0156 00AC 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0619 0156 0056 0015 0E6F";
        String downArrowPrevChannelCode = "0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 05F7 015B 0057 0016 0E6C";
        map.put(IR_SIGNAL.DOWN_ARROW.name(), count2duration(downArrowPrevChannelCode));

        return map;
    }

    /**
     * Transmit the correcsponding ir signal to the device
     *
     * @param irSignal type of the signal to be sent
     * @return true if transmission successfully completed and false if any error occurred
     */
    public boolean transmit(String irSignal) {
        try {
            IrSignal signal = irSignals.get(irSignal);
            consumerIrManager.transmit(signal.getFrequency(), signal.getDurationPattern());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * This function converts frequency count patter string to IrSignal object
     *
     * @param countPattern String count pattern
     * @return IrSignal object
     */
    protected IrSignal count2duration(String countPattern) {
        List<String> list = new ArrayList<>(Arrays.asList(countPattern.split(" ")));

        int frequency = Integer.valueOf(list.get(1), 16);

        list.remove(0);//dummy
        list.remove(0);//freq
        list.remove(0);//seq1
        list.remove(0);//seq2

        frequency = (int) (1000000 / (frequency * 0.241246));

        int pulses = 1000000 / frequency;
        int count;
        int duration;

        int[] durationPattern = new int[list.size()];
//        System.out.println("freq     = " + frequency);
//        System.out.print("pattern    =");
        for (int i = 0; i < list.size(); i++) {
            count = Integer.valueOf(list.get(i), 16);
            duration = count * pulses;
//            list.set(i, Integer.toString(duration));
            durationPattern[i] = duration;
//            System.out.print(" " + duration);
        }
        System.out.println();

        return new IrSignal(frequency, durationPattern);
    }


    public enum IR_SIGNAL {
        POWER,
        CH_UP, CH_DOWN, VOL_UP, VOL_DOWN,
        INPUT, QMENU, LIST, BACK, LAST, EXIT, MUTE, OK, MENU, INFO,
        RIGHT_ARROW, LEFT_ARROW, UP_ARROW, DOWN_ARROW,
        BLUE, YELLOW, RED, GREEN,
        NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9,

    }


    //=============================== Trash =================================
    //=============================== Trash =================================
    //=============================== Trash =================================
    protected String hex2dec(String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        for (int i = 0; i < list.size(); i++) {
            list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
        }

        frequency = (int) (1000000 / (frequency * 0.241246));
        list.add(0, Integer.toString(frequency));

        irData = "";
        for (String s : list) {
            irData += s + ",";
        }
        return irData;
    }


    public static int hex2Decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }


    // precondition:  d is a nonnegative integer
    public static String decimal2Hex(int d) {
        String digits = "0123456789ABCDEF";
        if (d == 0) return "0";
        String hex = "";
        while (d > 0) {
            int digit = d % 16;                // rightmost digit
            hex = digits.charAt(digit) + hex;  // string concatenation
            d = d / 16;
        }
        return hex;
    }

}