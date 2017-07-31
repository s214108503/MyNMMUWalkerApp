package walker.pack.customviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.ArrayList;

import walker.pack.R;
import walker.pack.TripSetupActivity;
import walker.pack.classes.Cell;
import walker.pack.interfaces.IndoorQRCodeInterface;

/**
 * Created by s214108503 on 2017/06/26.
 */

public class AnimationView extends View{

    private Paint brown_paint_brush_fill, brown_paint_brush_stroke;
    public Path start_directions, end_directions;

    private Intent data;

    public boolean drawDestination = false;

    public AnimationView(Context context, Intent data) {
        super(context);

        brown_paint_brush_fill = new Paint();
        brown_paint_brush_stroke = new Paint();
        start_directions = new Path();
        end_directions = new Path();
        this.data = data;

        brown_paint_brush_fill.setColor(Color.rgb(165,42,42));
        brown_paint_brush_fill.setStyle(Paint.Style.FILL);

        brown_paint_brush_stroke.setColor(Color.rgb(165,42,42));
        brown_paint_brush_stroke.setStyle(Paint.Style.STROKE);
        brown_paint_brush_stroke.setStrokeWidth(10);

        // TODO determine which floor plan to load
        setBackgroundResource(R.drawable.b9_02);
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

        Cell prev_cell = TripSetupActivity.start_path.get(0);//get first cell
        float x = mapToNewInterval(prev_cell.getX()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_W, 0, view_width);
        float y = mapToNewInterval(prev_cell.getY()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_H, 0, view_height);

        start_directions.moveTo(x, y);

        for (int cell_index = 1; cell_index < TripSetupActivity.start_path.size(); cell_index++){
            Cell cur = TripSetupActivity.start_path.get(cell_index);
            x = mapToNewInterval(cur.getX()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_W, 0, view_width);
            y = mapToNewInterval(cur.getY()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_H, 0, view_height);

            start_directions.lineTo(x, y);
            start_directions.moveTo(x, y);
        }

        boolean end_floor = data.getBooleanExtra("end_floor", false);
        // Check if there are directions for the destination node

        if (end_floor){
            prev_cell = TripSetupActivity.end_path.get(0);
            x = mapToNewInterval(prev_cell.getX()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_W, 0, view_width);
            y = mapToNewInterval(prev_cell.getY()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_H, 0, view_height);
            end_directions.moveTo(x, y);

            for (int cell_index = 1; cell_index < TripSetupActivity.end_path.size(); cell_index++){
                Cell cur = TripSetupActivity.end_path.get(cell_index);
                x = mapToNewInterval(cur.getX()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_W, 0, view_width);
                y = mapToNewInterval(cur.getY()*TripSetupActivity.CELL_SIZE, 0, TripSetupActivity.MAIN_H, 0, view_height);

                end_directions.lineTo(x, y);
                end_directions.moveTo(x, y);
            }

        }
    }

    private float mapToNewInterval(float value, float start1, float stop1, float start2, float stop2){
        return (value - start1) * (stop2 - start2) / (stop1 - start1) + start2;
    }

    public void clear(){
        start_directions.reset();
        end_directions.reset();
    }
}
