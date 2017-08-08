package walker.pack.ocr.things;

import android.content.Context;
import android.util.SparseArray;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

/**
 * Created by s214108503 on 2017/07/24.
 */

public class TextDetectorProcessor implements Detector.Processor<TextBlock> {

    private Context context;
    private EditText door_number_edit_text;
    private String building_floor_id;

    public TextDetectorProcessor(Context context, EditText door_number_edit_text, String building_floor_id){
        this.context = context;
        this.door_number_edit_text = door_number_edit_text;
        this.building_floor_id = building_floor_id;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        SparseArray<TextBlock> items = detections.getDetectedItems();
        // Iterates through an array of detected items (possibly door number)
        for (int i = 0; i < items.size(); ++i) {
            final TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {

                if (isNumeric(item.getValue())){
                    door_number_edit_text.post(new Runnable() {
                        @Override
                        public void run() {
                            switch (item.getValue().length()){
                                case 2:
                                case 3:
                                    // e.g. 05 or 05G
                                    door_number_edit_text.setText(building_floor_id+"_"+item.getValue());
                                    break;
                                case 4:
                                case 5:
                                    // e.g. 0201 or 0201B
                                    door_number_edit_text.setText(building_floor_id.substring(0, building_floor_id.indexOf("_"))+"_" + item.getValue().substring(0,2) + "_" + item.getValue().substring(2));
                                    break;
                                default:
                                    Toast.makeText(context, "Building not detected", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    // Checks if string is a numeric value
    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
}
