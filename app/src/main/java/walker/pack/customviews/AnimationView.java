package walker.pack.customviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import walker.pack.IndoorMapActivity;
import walker.pack.R;
import walker.pack.TripSetupActivity;
import walker.pack.classes.Cell;

/**
 * Created by s214108503 on 2017/06/26.
 */

public class AnimationView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {

    private Paint brown_paint_brush_fill, brown_paint_brush_stroke, transparent_paint_brush_stroke;
    public Path start_directions, end_directions;
    Paint clearPaint = new Paint();
    private Intent data;
    String start_id;
    public Bitmap green_flag_bitmap, red_flag_bitmap, background_bitmap, favourites_bitmap, poi_bitmap;
    public Drawable bitmapDrawable;
    public Canvas mCanvas;
    public ArrayList<Double[]> favourites, pois;

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    private float fScale = 1, fPosX = 0, fPosY = 0;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    public boolean drawDestination = false, drawPOIs = false, drawFavourites = false, clearCanvas = false;

    View animationView;

    public AnimationView(Context context) {
        super(context);
        init(((IndoorMapActivity) context).getIntent());
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(((IndoorMapActivity) context).getIntent());
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(((IndoorMapActivity) context).getIntent());
    }

    private void init(Intent data) {
        animationView = this;

        animationView.setOnTouchListener(this);

        this.data = data;
        start_id = data.getStringExtra("start_id");
        green_flag_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redflag16);
        red_flag_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.greenflag16);
        favourites_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iheart16);
        poi_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin16);
        brown_paint_brush_fill = new Paint();
        brown_paint_brush_stroke = new Paint();
        start_directions = new Path();
        start_directions.reset();
        end_directions = new Path();
        end_directions.reset();

        bitmapDrawable = this.getResources().getDrawable(R.drawable.person, null);

        favourites = new ArrayList<>();
        pois = new ArrayList<>();

        brown_paint_brush_fill.setColor(Color.rgb(165, 42, 42));
        brown_paint_brush_fill.setStyle(Paint.Style.FILL);

        brown_paint_brush_stroke.setColor(Color.rgb(7, 180, 237));
        //brown_paint_brush_stroke.setColor(Color.rgb(165, 42, 42));
        brown_paint_brush_stroke.setStyle(Paint.Style.STROKE);
        brown_paint_brush_stroke.setStrokeWidth(15);


        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        if (start_id.indexOf("4_00") != -1) {
            //setBackgroundResource(R.drawable.b4_00);
            //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b4_00);
            setImageResource( R.drawable.b4_00);
        } else if (start_id.indexOf("9_00") != -1) {
            //setBackgroundResource(R.drawable.b9_00);
            //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b9_00);
            setImageResource( R.drawable.b9_00);
        } else if (start_id.indexOf("9_01") != -1) {
            //setBackgroundResource(R.drawable.b9_01);
            //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b9_01);
            setImageResource( R.drawable.b9_01);
        } else if (start_id.indexOf("9_02") != -1) {
            //setBackgroundResource(R.drawable.b9_02);
            //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b9_02);
            setImageResource( R.drawable.b9_02);
        } else {
            //Toast.makeText(context, "Floor plan not found", Toast.LENGTH_SHORT).show();
            //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_background);
            setImageResource(R.drawable.white_background);
        }
        setDrawingCacheEnabled(true);

        //setImageBitmap(background_bitmap);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onDraw(Canvas canvas) {

/*        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.WHITE);*/

        super.onDraw(canvas);
        this.mCanvas = canvas;

        int width = this.getDrawable().getIntrinsicWidth(), height = this.getDrawable().getIntrinsicHeight();

        /*canvas.translate(fPosX, fPosY);
        canvas.scale(fScale, fScale);*/
        canvas.setMatrix(matrix);

        if (!clearCanvas) {
            initialiseDirections();
            // initially draw starting directions
            if (!drawDestination) {
                if (start_directions != null)
                    if (!start_directions.isEmpty()) {
                        canvas.drawPath(start_directions, brown_paint_brush_stroke);

                        Cell start_cell = TripSetupActivity.getStart_path().get(0);
                        float x = mapToNewInterval(start_cell.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, width);
                        float y = mapToNewInterval(start_cell.getY() * TripSetupActivity.getCellSize() - red_flag_bitmap.getHeight() + 15, 0, TripSetupActivity.getMainH(), 0, height);
                        canvas.drawBitmap(red_flag_bitmap, x, y, null);


                        Cell end_cell = TripSetupActivity.getStart_path().get(TripSetupActivity.getStart_path().size() - 1);
                        x = mapToNewInterval(end_cell.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, width);
                        y = mapToNewInterval(end_cell.getY() * TripSetupActivity.getCellSize() - green_flag_bitmap.getHeight() + 15, 0, TripSetupActivity.getMainH(), 0, height);
                        canvas.drawBitmap(green_flag_bitmap, x, y, null);
                    }
            } else {
                if (end_directions != null)
                    if (!end_directions.isEmpty()) {
                        canvas.drawPath(end_directions, brown_paint_brush_stroke);

                        Cell start_cell = TripSetupActivity.getEnd_path().get(0);
                        float x = mapToNewInterval(start_cell.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, width);
                        float y = mapToNewInterval(start_cell.getY() * TripSetupActivity.getCellSize() - red_flag_bitmap.getHeight() + 15, 0, TripSetupActivity.getMainH(), 0, height);
                        canvas.drawBitmap(red_flag_bitmap, x, y, null);

                        Cell end_cell = TripSetupActivity.getEnd_path().get(TripSetupActivity.getEnd_path().size() - 1);
                        x = mapToNewInterval(end_cell.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, width);
                        y = mapToNewInterval(end_cell.getY() * TripSetupActivity.getCellSize() - green_flag_bitmap.getHeight() + 15, 0, TripSetupActivity.getMainH(), 0, height);
                        canvas.drawBitmap(green_flag_bitmap, x, y, null);
                    }
            }

            if (drawFavourites) {
                float x;
                float y;
                for (Double[] xy : favourites) {
                    x = mapToNewInterval((float) (xy[0] * TripSetupActivity.getCellSize() + 11), 0, TripSetupActivity.getMainW(), 0, width);
                    y = mapToNewInterval((float) (xy[1] * TripSetupActivity.getCellSize() - favourites_bitmap.getHeight() ), 0, TripSetupActivity.getMainH(), 0, height);
                    canvas.drawBitmap(favourites_bitmap, x, y, null);
                }
            }

            if (drawPOIs) {
                float x;
                float y;
                for (Double[] xy : pois) {
                    x = mapToNewInterval((float) (xy[0] * TripSetupActivity.getCellSize()), 0, TripSetupActivity.getMainW(), 0, width);
                    y = mapToNewInterval((float) (xy[1] * TripSetupActivity.getCellSize() - poi_bitmap.getHeight()), 0, TripSetupActivity.getMainH(), 0, height);
                    canvas.drawBitmap(poi_bitmap, x, y, null);
                }
            }
        }

    }

    private void initialiseDirections() {
        // Get view width and height
        //int view_width = getWidth(), view_height = getHeight();
        int view_width = this.getDrawable().getIntrinsicWidth(), view_height = this.getDrawable().getIntrinsicHeight();

        Cell prev_cell = TripSetupActivity.getStart_path().get(0);//get first cell
        float x = mapToNewInterval(prev_cell.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
        float y = mapToNewInterval(prev_cell.getY() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);

        if (start_directions != null) {
            start_directions.moveTo(x, y);

            for (int cell_index = 1; cell_index < TripSetupActivity.getStart_path().size(); cell_index++) {
                Cell cur = TripSetupActivity.getStart_path().get(cell_index);
                x = mapToNewInterval(cur.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
                y = mapToNewInterval(cur.getY() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);

                if (cell_index < TripSetupActivity.getStart_path().size() - 1) {
                    if (mapToNewInterval(TripSetupActivity.getStart_path().get(0).getY() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height)
                            >= mapToNewInterval(TripSetupActivity.getStart_path().get(TripSetupActivity.getStart_path().size()-1).getY() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height)) {
                        start_directions.lineTo(x + 5, y );
                        start_directions.moveTo(x + 5, y );
                    } else {
                        start_directions.lineTo(x, y + 5);
                        start_directions.moveTo(x, y + 5);
                    }
                } else {
                    start_directions.lineTo(x, y);
                    start_directions.moveTo(x, y);
                }

            }
        } else {
            start_directions = new Path();
        }

        boolean end_floor = data.getBooleanExtra("end_floor", false);
        // Check if there are directions for the destination node
        if (end_directions != null) {
            if (end_floor) {
                prev_cell = TripSetupActivity.getEnd_path().get(0);
                x = mapToNewInterval(prev_cell.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
                y = mapToNewInterval(prev_cell.getY() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);
                end_directions.moveTo(x, y);

                for (int cell_index = 1; cell_index < TripSetupActivity.getEnd_path().size(); cell_index++) {
                    Cell cur = TripSetupActivity.getEnd_path().get(cell_index);
                    x = mapToNewInterval(cur.getX() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
                    y = mapToNewInterval(cur.getY() * TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);

                    if (cell_index < TripSetupActivity.getEnd_path().size() - 1) {
                        end_directions.lineTo(x + 5, y);
                        end_directions.moveTo(x + 5, y);
                    } else {
                        end_directions.lineTo(x, y + 5);
                        end_directions.moveTo(x, y + 5);
                    }
                }
            }
        } else {
            end_directions = new Path();
        }
    }

    public float mapToNewInterval(float value, float start1, float stop1, float start2, float stop2) {
        return (value - start1) * (stop2 - start2) / (stop1 - start1) + start2;
    }

    public void clear() {
        TripSetupActivity.getStart_path().clear();
        start_directions.reset();
        TripSetupActivity.getEnd_path().clear();
        end_directions.reset();
        invalidate();
        System.gc();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        fScale = scale;
                    }
                }
                break;
        }

        fPosX = start.x;
        fPosY = start.y;

        view.setImageMatrix(matrix); // display the transformation on screen
        //start_directions.transform(matrix);
        invalidate();
        //setScaleType(ScaleType.FIT_XY);

        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
    }

}
