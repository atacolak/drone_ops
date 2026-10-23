package sh.colak.dji.aoaread;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Lean session owner. Keepalive TX only (0x06/0x77). Inbound 0x06/0x05 is REPORT.
 * Never sends 0x03/0x8E, 0x03/0x2A, or stick frames.
 */
public class CaptureActivity extends Activity {
    private static final String TAG = "AoaRead";
    private static final String ACTION_PERM = "sh.colak.dji.aoaread.USB_PERMISSION";
    private UsbManager usb;
    private ParcelFileDescriptor pfd;
    private FileInputStream in;
    private FileOutputStream out;
    private Thread reader;
    private Thread keep;
    private volatile boolean running;
    private int seq = 1;
    private TextView tv;
    private String status = "no accessory";
    private FileOutputStream unitOut;
    private FileOutputStream idxOut;
    private int n574a, n5749, nOther, lossResync, lossOversize;

    private final BroadcastReceiver permRx = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) {
            if (!ACTION_PERM.equals(i.getAction())) return;
            UsbAccessory acc = i.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
            if (i.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && acc != null) {
                open(acc);
            } else {
                status = "usb permission denied";
                paint();
            }
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = new TextView(this);
        tv.setBackgroundColor(0xFF111318);
        tv.setTextColor(0xFFE8EAED);
        tv.setTextSize(18);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(40, 40, 40, 40);
        setContentView(tv);
        usb = (UsbManager) getSystemService(USB_SERVICE);
        registerReceiver(permRx, new IntentFilter(ACTION_PERM), RECEIVER_NOT_EXPORTED);
        UsbAccessory acc = getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
        if (acc == null) {
            UsbAccessory[] list = usb.getAccessoryList();
            if (list != null && list.length > 0) acc = list[0];
        }
        if (acc == null) {
            status = "no accessory\nforce-stop Fly, plug TOP cradle";
            paint();
            return;
        }
        if (!usb.hasPermission(acc)) {
            status = "requesting usb…";
            paint();
            Intent intent = new Intent(ACTION_PERM);
            intent.setPackage(getPackageName());
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            usb.requestPermission(acc, pi);
            return;
        }
        open(acc);
    }

    private void open(UsbAccessory acc) {
        try {
            pfd = usb.openAccessory(acc);
            if (pfd == null) {
                status = "openAccessory null (Fly still owns fd?)";
                paint();
                return;
            }
            in = new FileInputStream(pfd.getFileDescriptor());
            out = new FileOutputStream(pfd.getFileDescriptor());
            running = true;
            reader = new Thread(this::loop, "aoa-read");
            reader.start();
            keep = new Thread(this::keepLoop, "aoa-ka");
            keep.start();
            status = "session up (no Fly)\nkeepalive 0x06/0x77 only\nwaiting 0x06/0x05…";
            paint();
            Log.i(TAG, "openAccessory ok lean owner");
        } catch (Exception e) {
            status = "open failed " + e.getMessage();
            paint();
        }
    }

    private void keepLoop() {
        while (running) {
            sendKeepalive();
            try { Thread.sleep(2500); } catch (InterruptedException e) { break; }
        }
    }

    private void sendKeepalive() {
        if (out == null) return;
        try {
            byte[] a = Duml.keepalive(0x06, seq++);
            byte[] b = Duml.keepalive(0x0E, seq++);
            synchronized (out) {
                out.write(a);
                out.write(b);
                out.flush();
            }
        } catch (Exception e) {
            Log.e(TAG, "keepalive TX failed", e);
        }
    }

    private void loop() {
        byte[] buf = new byte[16384];
        ByteArrayOutputStream acc = new ByteArrayOutputStream();
        try {
            java.io.File dir = getExternalFilesDir(null);
            if (dir == null) dir = getFilesDir();
            unitOut = new FileOutputStream(new java.io.File(dir, "units.rclink"));
            idxOut = new FileOutputStream(new java.io.File(dir, "units.idx"));
            while (running) {
                int n = in.read(buf);
                if (n < 0) break;
                if (n == 0) continue;
                acc.write(buf, 0, n);
                consume(acc);
            }
        } catch (IOException e) {
            Log.e(TAG, "read loop", e);
        } finally {
            try { if (unitOut != null) unitOut.close(); } catch (Exception ignored) {}
            try { if (idxOut != null) idxOut.close(); } catch (Exception ignored) {}
        }
        Log.i(TAG, "loop done n574a=" + n574a + " n5749=" + n5749 + " nOther=" + nOther
                + " lossResync=" + lossResync + " lossOversize=" + lossOversize);
    }

    private void consume(ByteArrayOutputStream acc) throws IOException {
        byte[] d = acc.toByteArray();
        int i = 0;
        while (i + 8 <= d.length) {
            if ((d[i] & 0xff) != 0x55 || (d[i + 1] & 0xff) != 0xCC) {
                i++;
                lossResync++;
                continue;
            }
            int ln = (d[i + 4] & 0xff) | ((d[i + 5] & 0xff) << 8)
                    | ((d[i + 6] & 0xff) << 16) | ((d[i + 7] & 0xff) << 24);
            if (ln < 0 || ln > 0x200000) {
                i++;
                lossOversize++;
                continue;
            }
            if (i + 8 + ln > d.length) break;
            int type = (d[i + 2] & 0xff) | ((d[i + 3] & 0xff) << 8);
            byte[] p = new byte[ln];
            System.arraycopy(d, i + 8, p, 0, ln);
            long ts = System.currentTimeMillis();
            unitOut.write(d, i, 8 + ln);
            String idx = ts + " type=" + Integer.toHexString(type) + " len=" + ln
                    + " off=" + (8 + ln) + "\n";
            idxOut.write(idx.getBytes("UTF-8"));
            if (type == 0x574a) n574a++;
            else if (type == 0x5749) n5749++;
            else nOther++;
            onUnit(type, p);
            i += 8 + ln;
        }
        if (i > 0) {
            acc.reset();
            acc.write(d, i, d.length - i);
        }
    }

    private void onUnit(int type, byte[] p) {
        if (type != 0x5749 || p.length < 13 || (p[0] & 0xff) != 0x55) return;
        if ((p[9] & 0xff) != 0x06 || (p[10] & 0xff) != 0x05) return;
        if (p.length < 11 + 10 + 2) return;
        int[] ch = new int[5];
        for (int k = 0; k < 5; k++) {
            int o = 11 + k * 2;
            ch[k] = (p[o] & 0xff) | ((p[o + 1] & 0xff) << 8);
        }
        final String line = "AoaRead  no Fly  no inject\n\n"
                + "ch0 " + bar(ch[0]) + "\n"
                + "ch1 " + bar(ch[1]) + "\n"
                + "ch2 " + bar(ch[2]) + "\n"
                + "ch3 " + bar(ch[3]) + "\n"
                + "ch4 " + bar(ch[4]) + "\n\n"
                + "364 / 1024 / 1684  REPORT only";
        runOnUiThread(() -> { status = line; paint(); });
    }

    private static String bar(int v) {
        int x = v;
        if (x < 364) x = 364;
        if (x > 1684) x = 1684;
        int n = (x - 364) * 16 / (1684 - 364);
        StringBuilder sb = new StringBuilder(24);
        sb.append(String.format("%4d ", v));
        for (int i = 0; i < 16; i++) sb.append(i == n ? "|" : "-");
        return sb.toString();
    }

    private void paint() {
        if (tv != null) tv.setText(status);
    }

    @Override protected void onDestroy() {
        running = false;
        try { unregisterReceiver(permRx); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (pfd != null) pfd.close(); } catch (Exception ignored) {}
        super.onDestroy();
    }
}
