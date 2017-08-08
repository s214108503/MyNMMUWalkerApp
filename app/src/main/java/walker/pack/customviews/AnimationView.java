package walker.pack.customviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.widget.Toast;

import walker.pack.R;
import walker.pack.TripSetupActivity;
import walker.pack.classes.Cell;

/**
 * Created by s214108503 on 2017/06/26.
 */

public class AnimationView extends View{

    private Paint brown_paint_brush_fill, brown_paint_brush_stroke;
    public Path start_directions, end_directions;

    private Intent data;
    String start_id;

    public boolean drawDestination = false;

    public AnimationView(Context context, Intent data) {
        super(context);

        brown_paint_brush_fill = new Paint();
        brown_paint_brush_stroke = new Paint();
        start_directions = new Path();
        end_directions = new Path();
        this.data = data;
        start_id = data.getStringExtra("start_id");

        brown_paint_brush_fill.setColor(Color.rgb(165,42,42));
        brown_paint_brush_fill.setStyle(Paint.Style.FILL);

        brown_paint_brush_stroke.setColor(Color.rgb(165,42,42));
        brown_paint_brush_stroke.setStyle(Paint.Style.STROKE);
        brown_paint_brush_stroke.setStrokeWidth(10);

        if (start_id.indexOf("4_00") != -1) {
            setBackgroundResource(R.drawable.b4_00);
        } else if (start_id.indexOf("9_00") != -1) {
            setBackgroundResource(R.drawable.b9_00);
        } else if (start_id.indexOf("9_01") != -1) {
            setBackgroundResource(R.drawable.b9_01);
        } else if (start_id.indexOf("9_02") != -1) {
            setBackgroundResource(R.drawable.b9_02);
        } else {
            Toast.makeText(context, "Floor plan not found", Toast.LENGTH_SHORT).show();
            setBackgroundResource(R.color.colorBackground);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initialiseDirections();
        // initially draw starting directions
        if (!drawDestination) {
            canvas.drawPath(start_directions, brown_paint_brush_stroke);
        }else {
            start_directions.reset();
            canvas.drawPath(end_directions, brown_paint_brush_stroke);
        }
    }

    private void initialiseDirections(){
        // Get view width and height
        int view_width = getWidth(), view_height = getHeight();

        Cell prev_cell = TripSetupActivity.getStart_path().get(0);//get first cell
        float x = mapToNewInterval(prev_cell.getX()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
        float y = mapToNewInterval(prev_cell.getY()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);

        start_directions.moveTo(x, y);

        for (int cell_index = 1; cell_index < TripSetupActivity.getStart_path().size(); cell_index++){
            Cell cur = TripSetupActivity.getStart_path().get(cell_index);
            x = mapToNewInterval(cur.getX()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
            y = mapToNewInterval(cur.getY()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);

            start_directions.lineTo(x, y);
            if (cell_index < TripSetupActivity.getStart_path().size()-1)
                start_directions.moveTo(x, y);
        }

        boolean end_floor = data.getBooleanExtra("end_floor", false);
        // Check if there are directions for the destination node

        if (end_floor){
            prev_cell = TripSetupActivity.getEnd_path().get(0);
            x = mapToNewInterval(prev_cell.getX()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
            y = mapToNewInterval(prev_cell.getY()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);
            end_directions.moveTo(x, y);

            for (int cell_index = 1; cell_index < TripSetupActivity.getEnd_path().size(); cell_index++){
                Cell cur = TripSetupActivity.getEnd_path().get(cell_index);
                x = mapToNewInterval(cur.getX()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainW(), 0, view_width);
                y = mapToNewInterval(cur.getY()*TripSetupActivity.getCellSize(), 0, TripSetupActivity.getMainH(), 0, view_height);

                end_directions.lineTo(x, y);
                if (cell_index < TripSetupActivity.getStart_path().size()-1)
                    end_directions.moveTo(x, y);

            }
        }
    }

    private float mapToNewInterval(float value, float start1, float stop1, float start2, float stop2){
        return (value - start1) * (stop2 - start2) / (stop1 - start1) + start2;
    }

    public void clear(){
        start_directions.rewind();
        start_directions.reset();
        end_directions.rewind();
        end_directions.reset();
    }
}
