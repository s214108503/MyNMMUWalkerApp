package walker.pack.customviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 * Created by s214108503 on 2017/07/26.
 */

public class TempIndoorMapVIew extends android.support.v7.widget.AppCompatImageView {

    private Paint brown_paint_brush_stroke;

    private Intent data;



    public TempIndoorMapVIew(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
