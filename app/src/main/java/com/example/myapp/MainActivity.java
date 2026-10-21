package com.example.myapp;

import android.app.Activity;import React, { useState } from "react";
import {
  Alert,
  Image,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import * as ImagePicker from "expo-image-picker";

export default function App() {
  const [tab, setTab] = useState("portfolio");
  const [works, setWorks] = useState([]);
  const [orders, setOrders] = useState([]);
  const [clients, setClients] = useState([
    { name: "عميل تجريبي", email: "client@example.com" },
  ]);

  const [title, setTitle] = useState("");
  const [clientName, setClientName] = useState("");
  const [service, setService] = useState("");
  const [details, setDetails] = useState("");

  async function uploadWork() {
    const permission = await ImagePicker.requestMediaLibraryPermissionsAsync();

    if (!permission.granted) {
      Alert.alert("إذن مطلوب", "اسمحي للتطبيق بالوصول إلى الصور أولًا.");
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ["images"],
      allowsEditing: true,
      quality: 0.8,
    });

    if (!result.canceled) {
      setWorks((current) => [
        { id: Date.now().toString(), title: title || "عمل جديد", image: result.assets[0].uri },
        ...current,
      ]);
      setTitle("");
    }
  }

  function submitOrder() {
    if (!clientName.trim() || !service.trim()) {
      Alert.alert("بيانات ناقصة", "اكتبي اسمك ونوع التصميم المطلوب.");
      return;
    }

    const newOrder = {
      id: Date.now().toString(),
      clientName,
      service,
      details,
      status: "قيد المراجعة",
    };

    setOrders((current) => [newOrder, ...current]);
    setClients((current) =>
      current.some((client) => client.name === clientName)
        ? current
        : [...current, { name: clientName, email: "—" }]
    );

    setClientName("");
    setService("");
    setDetails("");
    Alert.alert("تم إرسال الطلب", "سأتواصل معك لمراجعة التفاصيل.");
  }

  function renderPortfolio() {
    return (
      <View>
        <Text style={styles.heading}>معرض الأعمال</Text>

        <TextInput
          style={styles.input}
          placeholder="عنوان العمل الجديد"
          value={title}
          onChangeText={setTitle}
          textAlign="right"
        />

        <TouchableOpacity style={styles.button} onPress={uploadWork}>
          <Text style={styles.buttonText}>＋ رفع عمل من الجهاز</Text>
        </TouchableOpacity>

        {works.length === 0 ? (
          <Text style={styles.empty}>أضيفي أعمالك لتظهر هنا.</Text>
        ) : (
          works.map((work) => (
            <View style={styles.card} key={work.id}>
              <Image source={{ uri: work.image }} style={styles.workImage} />
              <Text style={styles.cardTitle}>{work.title}</Text>
            </View>
          ))
        )}
      </View>
    );
  }

  function renderOrderForm() {
    return (
      <View>
        <Text style={styles.heading}>اطلبي تصميمًا</Text>

        <TextInput
          style={styles.input}
          placeholder="اسمك"
          value={clientName}
          onChangeText={setClientName}
          textAlign="right"
        />
        <TextInput
          style={styles.input}
          placeholder="نوع التصميم (شعار، منشور، هوية بصرية...)"
          value={service}
          onChangeText={setService}
          textAlign="right"
        />
        <TextInput
          style={[styles.input, styles.multiline]}
          placeholder="تفاصيل الطلب"
          value={details}
          onChangeText={setDetails}
          multiline
          textAlign="right"
        />

        <TouchableOpacity style={styles.button} onPress={submitOrder}>
          <Text style={styles.buttonText}>إرسال الطلب</Text>
        </TouchableOpacity>
      </View>
    );
  }

  function renderAdmin() {
    return (
      <View>
        <Text style={styles.heading}>لوحة الإدارة</Text>

        <Text style={styles.subheading}>العملاء ({clients.length})</Text>
        {clients.map((client, index) => (
          <View style={styles.card} key={`${client.name}-${index}`}>
            <Text style={styles.cardTitle}>{client.name}</Text>
            <Text style={styles.muted}>{client.email}</Text>
          </View>
        ))}

        <Text style={styles.subheading}>الطلبات ({orders.length})</Text>
        {orders.length === 0 ? (
          <Text style={styles.empty}>لا توجد طلبات حتى الآن.</Text>
        ) : (
          orders.map((order) => (
            <View style={styles.card} key={order.id}>
              <Text style={styles.cardTitle}>{order.service}</Text>
              <Text style={styles.muted}>العميل: {order.clientName}</Text>
              <Text style={styles.muted}>الحالة: {order.status}</Text>
              {!!order.details && <Text style={styles.muted}>{order.details}</Text>}
            </View>
          ))
        )}
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.appTitle}>استوديو التصميم</Text>

      <View style={styles.tabs}>
        <Tab label="الأعمال" active={tab === "portfolio"} onPress={() => setTab("portfolio")} />
        <Tab label="اطلب تصميمًا" active={tab === "order"} onPress={() => setTab("order")} />
        <Tab label="الإدارة" active={tab === "admin"} onPress={() => setTab("admin")} />
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {tab === "portfolio" && renderPortfolio()}
        {tab === "order" && renderOrderForm()}
        {tab === "admin" && renderAdmin()}
      </ScrollView>
    </View>
  );
}

function Tab({ label, active, onPress }) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={[styles.tab, active && styles.activeTab]}
    >
      <Text style={[styles.tabText, active && styles.activeTabText]}>{label}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#F7F5F2", paddingTop: 55 },
  appTitle: {
    fontSize: 24,
    fontWeight: "bold",
    textAlign: "center",
    marginBottom: 18,
    color: "#26222A",
  },
  tabs: { flexDirection: "row", justifyContent: "center", paddingHorizontal: 10 },
  tab: {
    paddingVertical: 11,
    paddingHorizontal: 13,
    borderRadius: 20,
    marginHorizontal: 3,
    backgroundColor: "#EAE6E1",
  },
  activeTab: { backgroundColor: "#5D3FD3" },
  tabText: { color: "#37313E", fontSize: 13 },
  activeTabText: { color: "#FFFFFF", fontWeight: "bold" },
  content: { padding: 18, paddingBottom: 40 },
  heading: { fontSize: 22, fontWeight: "bold", textAlign: "right", marginBottom: 14 },
  subheading: { fontSize: 18, fontWeight: "bold", textAlign: "right", marginTop: 20, marginBottom: 8 },
  input: {
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    padding: 14,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: "#E4DFE8",
  },
  multiline: { minHeight: 100, textAlignVertical: "top" },
  button: {
    backgroundColor: "#5D3FD3",
    padding: 14,
    borderRadius: 12,
    alignItems: "center",
    marginBottom: 14,
  },
  buttonText: { color: "#FFFFFF", fontWeight: "bold", fontSize: 16 },
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 14,
    padding: 14,
    marginBottom: 12,
  },
  cardTitle: { fontWeight: "bold", fontSize: 16, textAlign: "right" },
  muted: { color: "#6C6672", textAlign: "right", marginTop: 5 },
  empty: { color: "#77717C", textAlign: "center", padding: 20 },
  workImage: { width: "100%", height: 210, borderRadius: 10, marginBottom: 10 },
});

import android.app.AlertDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * لوحة رسم بسيطة — قالب KlencodIDE
 *
 * الميزات:
 *   - رسم بالإصبع (Canvas + Path)
 *   - 8 ألوان
 *   - 3 سماكات فرشاة
 *   - تراجع (Undo)
 *   - مسح الكل
 *   - حفظ PNG في Pictures/KlencodDrawings (Android 10+)
 *
 * © 2026 KlencodIDE
 */
public class MainActivity extends Activity {

    // ============================================================
    // الألوان الأساسية (نفس ألوان KlencodIDE)
    // ============================================================
    private static final int COLOR_BG = 0xFF0D1117;
    private static final int COLOR_SURFACE = 0xFF161B22;
    private static final int COLOR_SURFACE_ALT = 0xFF21262D;
    private static final int COLOR_PRIMARY = 0xFF1F6FEB;
    private static final int COLOR_PRIMARY_LIGHT = 0xFF58A6FF;
    private static final int COLOR_TEXT = 0xFFE6EDF3;
    private static final int COLOR_MUTED = 0xFF8B949E;
    private static final int COLOR_BORDER = 0xFF30363D;

    // 8 ألوان للرسم
    private static final int[] PALETTE = {
            0xFFFFFFFF, // أبيض
            0xFF000000, // أسود
            0xFFFF7B72, // أحمر
            0xFF7EE787, // أخضر
            0xFF58A6FF, // أزرق
            0xFFD2A8FF, // بنفسجي
            0xFFFFD33D, // أصفر
            0xFFFF9E64  // برتقالي
    };

    // 3 سماكات
    private static final float[] BRUSH_SIZES = {4f, 10f, 22f};

    // ============================================================
    // الحالة
    // ============================================================
    private DrawingView drawingView;
    private int currentColorIndex = 2;  // أحمر افتراضي
    private int currentSizeIndex = 1;   // متوسط افتراضي
    private final ArrayList<Button> colorButtons = new ArrayList<>();
    private final ArrayList<Button> sizeButtons = new ArrayList<>();

    // ============================================================
    // dp helper
    // ============================================================
    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    // ============================================================
    // onCreate
    // ============================================================
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(COLOR_BG);
        getWindow().setNavigationBarColor(COLOR_BG);

        // Root
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(COLOR_BG);

        // ============================================================
        // الترويسة العلوية
        // ============================================================
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(14), dp(10), dp(14), dp(10));
        header.setBackgroundColor(COLOR_SURFACE);

        TextView title = new TextView(this);
        title.setText("🎨 لوحة الرسم");
        title.setTextColor(COLOR_TEXT);
        title.setTextSize(17);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        header.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));

        Button undoBtn = makeTopButton("↶ تراجع");
        undoBtn.setOnClickListener(v -> drawingView.undo());
        header.addView(undoBtn, new LinearLayout.LayoutParams(-2, dp(40)));

        Button clearBtn = makeTopButton("🗑 مسح");
        clearBtn.setOnClickListener(v -> confirmClear());
        LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(-2, dp(40));
        clearParams.leftMargin = dp(6);
        header.addView(clearBtn, clearParams);

        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        // ============================================================
        // منطقة الرسم
        // ============================================================
        FrameLayout drawContainer = new FrameLayout(this);
        drawContainer.setBackgroundColor(Color.WHITE);

        drawingView = new DrawingView(this);
        drawContainer.addView(drawingView, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout.LayoutParams drawParams = new LinearLayout.LayoutParams(-1, 0, 1f);
        drawParams.setMargins(dp(12), dp(12), dp(12), dp(12));
        root.addView(drawContainer, drawParams);

        // ============================================================
        // شريط التحكم السفلي
        // ============================================================
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setPadding(dp(12), dp(10), dp(12), dp(12));
        controls.setBackgroundColor(COLOR_SURFACE);

        // --- صف الألوان ---
        TextView colorLabel = new TextView(this);
        colorLabel.setText("اللون:");
        colorLabel.setTextColor(COLOR_MUTED);
        colorLabel.setTextSize(12);
        controls.addView(colorLabel);

        LinearLayout colorRow = new LinearLayout(this);
        colorRow.setOrientation(LinearLayout.HORIZONTAL);
        colorRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams colorRowParams = new LinearLayout.LayoutParams(-1, dp(50));
        colorRowParams.topMargin = dp(6);
        controls.addView(colorRow, colorRowParams);

        for (int i = 0; i < PALETTE.length; i++) {
            final int index = i;
            Button colorBtn = new Button(this);
            colorBtn.setBackground(makeCircleDrawable(PALETTE[i]));
            colorBtn.setPadding(0, 0, 0, 0);
            colorBtn.setOnClickListener(v -> selectColor(index));

            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, dp(40), 1f);
            cp.setMargins(dp(3), 0, dp(3), 0);
            colorRow.addView(colorBtn, cp);
            colorButtons.add(colorBtn);
        }

        // --- صف السماكات ---
        TextView sizeLabel = new TextView(this);
        sizeLabel.setText("سماكة الفرشاة:");
        sizeLabel.setTextColor(COLOR_MUTED);
        sizeLabel.setTextSize(12);
        LinearLayout.LayoutParams sizeLabelParams = new LinearLayout.LayoutParams(-1, -2);
        sizeLabelParams.topMargin = dp(12);
        controls.addView(sizeLabel, sizeLabelParams);

        LinearLayout sizeRow = new LinearLayout(this);
        sizeRow.setOrientation(LinearLayout.HORIZONTAL);
        sizeRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams sizeRowParams = new LinearLayout.LayoutParams(-1, dp(46));
        sizeRowParams.topMargin = dp(6);
        controls.addView(sizeRow, sizeRowParams);

        String[] sizeNames = {"رفيع", "متوسط", "عريض"};
        for (int i = 0; i < BRUSH_SIZES.length; i++) {
            final int index = i;
            Button sizeBtn = new Button(this);
            sizeBtn.setText(sizeNames[i]);
            sizeBtn.setTextSize(12);
            sizeBtn.setAllCaps(false);
            sizeBtn.setPadding(0, 0, 0, 0);
            sizeBtn.setOnClickListener(v -> selectSize(index));

            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, dp(42), 1f);
            sp.setMargins(dp(4), 0, dp(4), 0);
            sizeRow.addView(sizeBtn, sp);
            sizeButtons.add(sizeBtn);
        }

        // --- صف الحفظ ---
        Button saveBtn = new Button(this);
        saveBtn.setText("💾 حفظ الصورة");
        saveBtn.setTextSize(14);
        saveBtn.setAllCaps(false);
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        GradientDrawable saveBg = new GradientDrawable();
        saveBg.setColor(0xFF238636);
        saveBg.setCornerRadius(dp(12));
        saveBg.setStroke(dp(1), 0xFF56D364);
        saveBtn.setBackground(saveBg);
        saveBtn.setElevation(dp(2));
        saveBtn.setOnClickListener(v -> saveImage());

        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(-1, dp(50));
        saveParams.topMargin = dp(14);
        controls.addView(saveBtn, saveParams);

        root.addView(controls, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);

        // الاختيارات الافتراضية
        selectColor(currentColorIndex);
        selectSize(currentSizeIndex);
    }

    // ============================================================
    // دوال مساعدة للواجهة
    // ============================================================
    private Button makeTopButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(12);
        btn.setAllCaps(false);
        btn.setTextColor(COLOR_TEXT);
        btn.setPadding(dp(12), 0, dp(12), 0);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_SURFACE_ALT);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), COLOR_BORDER);
        btn.setBackground(bg);
        return btn;
    }

    private GradientDrawable makeCircleDrawable(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
        d.setStroke(dp(2), COLOR_BORDER);
        return d;
    }

    private GradientDrawable makeSelectedCircleDrawable(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
        d.setStroke(dp(3), COLOR_PRIMARY_LIGHT);
        return d;
    }

    private GradientDrawable makeSizeButtonDrawable(boolean selected) {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(10));
        d.setColor(selected ? COLOR_PRIMARY : COLOR_SURFACE_ALT);
        d.setStroke(dp(1), selected ? COLOR_PRIMARY_LIGHT : COLOR_BORDER);
        return d;
    }

    private void selectColor(int index) {
        currentColorIndex = index;
        drawingView.setColor(PALETTE[index]);

        for (int i = 0; i < colorButtons.size(); i++) {
            Button b = colorButtons.get(i);
            if (i == index) {
                b.setBackground(makeSelectedCircleDrawable(PALETTE[i]));
            } else {
                b.setBackground(makeCircleDrawable(PALETTE[i]));
            }
        }
    }

    private void selectSize(int index) {
        currentSizeIndex = index;
        drawingView.setStrokeWidth(BRUSH_SIZES[index]);

        for (int i = 0; i < sizeButtons.size(); i++) {
            Button b = sizeButtons.get(i);
            b.setBackground(makeSizeButtonDrawable(i == index));
            b.setTextColor(i == index ? Color.WHITE : COLOR_TEXT);
        }
    }

    // ============================================================
    // حفظ الصورة
    // ============================================================
    private void saveImage() {
        try {
            Bitmap bitmap = drawingView.exportBitmap();
            if (bitmap == null) {
                Toast.makeText(this, "لا يوجد شيء لحفظه", Toast.LENGTH_SHORT).show();
                return;
            }

            String timestamp = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String fileName = "drawing_" + timestamp + ".png";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/KlencodDrawings");

                Uri uri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if (uri == null) {
                    Toast.makeText(this, "تعذر إنشاء ملف", Toast.LENGTH_SHORT).show();
                    return;
                }

                OutputStream out = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                if (out != null) out.close();
                bitmap.recycle();

                Toast.makeText(this,
                        "✅ تم حفظ الصورة في:\nPictures/KlencodDrawings/" + fileName,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "يحتاج Android 10+ للحفظ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void confirmClear() {
        new AlertDialog.Builder(this)
                .setTitle("مسح اللوحة")
                .setMessage("هل أنت متأكد من مسح كل الرسم؟")
                .setPositiveButton("مسح", (d, w) -> drawingView.clear())
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ============================================================
    // DrawingView — Canvas مخصص
    // ============================================================
    private static class DrawingView extends View {

        private final ArrayList<Stroke> strokes = new ArrayList<>();
        private Stroke currentStroke;

        private int currentColor = Color.RED;
        private float currentStrokeWidth = 10f;

        public DrawingView(Activity context) {
            super(context);
            setFocusable(true);
            setBackgroundColor(Color.WHITE);
        }

        public void setColor(int color) {
            this.currentColor = color;
        }

        public void setStrokeWidth(float width) {
            this.currentStrokeWidth = width;
        }

        public void clear() {
            strokes.clear();
            currentStroke = null;
            invalidate();
        }

        public void undo() {
            if (strokes.isEmpty()) return;
            strokes.remove(strokes.size() - 1);
            invalidate();
        }

        public Bitmap exportBitmap() {
            if (getWidth() <= 0 || getHeight() <= 0) return null;
            Bitmap bmp = Bitmap.createBitmap(
                    getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(Color.WHITE);

            for (Stroke stroke : strokes) {
                stroke.draw(canvas);
            }
            if (currentStroke != null) {
                currentStroke.draw(canvas);
            }
            return bmp;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (Stroke stroke : strokes) {
                stroke.draw(canvas);
            }

            if (currentStroke != null) {
                currentStroke.draw(canvas);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentStroke = new Stroke(currentColor, currentStrokeWidth);
                    currentStroke.addPoint(x, y);
                    invalidate();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (currentStroke != null) {
                        currentStroke.addPoint(x, y);
                        invalidate();
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (currentStroke != null) {
                        strokes.add(currentStroke);
                        currentStroke = null;
                        invalidate();
                    }
                    return true;
            }
            return super.onTouchEvent(event);
        }
    }

    // ============================================================
    // Stroke — ضربة واحدة (مسار واحد)
    // ============================================================
    private static class Stroke {

        private final Paint paint;
        private final Path path;

        public Stroke(int color, float strokeWidth) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setDither(true);

            path = new Path();
        }

        public void addPoint(float x, float y) {
            if (path.isEmpty()) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }
}