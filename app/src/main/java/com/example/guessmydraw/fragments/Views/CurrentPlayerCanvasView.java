package com.example.guessmydraw.fragments.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.fragments.CanvasCurrentPlayer;
import com.example.guessmydraw.utilities.GameViewModel;

public class CurrentPlayerCanvasView extends View {

    //viewModel used to save all the information needed for the match
    private final GameViewModel gameViewModel;

    private final int touchTolerance;
    private Canvas extraCanvas;
    private Bitmap extraBitmap;
    private final Path path;
    private final Paint paint;

    /*attributes to store screen coordinates used for drawing*/
    private float motionTouchEventX;
    private float motionTouchEventY;
    private float currentX;
    private float currentY;

    public CurrentPlayerCanvasView(Context context, AttributeSet attrs) {

        super(context, attrs);
        touchTolerance = ViewConfiguration.get(context).getScaledTouchSlop();

        gameViewModel = new ViewModelProvider(((MainActivity) getContext())).get(GameViewModel.class);

        //set the color we will use to paint
        int paintColor = ResourcesCompat.getColor(this.getResources(), R.color.colorPaint, null);

        path = new Path();

        paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12.0F);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (extraBitmap != null) {
            extraBitmap.recycle();
        }

        extraBitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
        extraCanvas = new Canvas();

        if (gameViewModel.getBitmap() != null){
            extraBitmap = gameViewModel.getBitmap();
        }

        extraCanvas.setBitmap(extraBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) { //this canvas is not our this.extraCanvas
        super.onDraw(canvas);
        canvas.drawBitmap(extraBitmap, 0f, 0f, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //we save the current bitmap to restore it when the view is recreated
        gameViewModel.setBitmap(extraBitmap);
    }

    public void changePaintColor(int color){
        this.paint.setColor(color);
    }

    public boolean onTouchEvent(MotionEvent event) {

        this.motionTouchEventX = event.getX();
        this.motionTouchEventY = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.touchStart();
                break;
            case MotionEvent.ACTION_UP:
                this.touchUp();
                break;
            case MotionEvent.ACTION_MOVE:
                this.touchMove();
                break;
        }
        return true;
    }

    private void touchStart() {

        sendTouchStartMessage(this.motionTouchEventX, this.motionTouchEventY);
        this.path.reset();
        this.path.moveTo(this.motionTouchEventX, this.motionTouchEventY);
        this.currentX = this.motionTouchEventX;
        this.currentY = this.motionTouchEventY;
    }

    private void touchMove() {

        float dx = Math.abs(this.motionTouchEventX - this.currentX);
        float dy = Math.abs(this.motionTouchEventY - this.currentY);
        if (dx >= (float)this.touchTolerance || dy >= (float)this.touchTolerance) {

            float x2 = (this.motionTouchEventX + this.currentX) / (float)2;
            float y2 = (this.motionTouchEventY + this.currentY) / (float)2;

            sendTouchMoveMessage(this.currentX, this.currentY, x2, y2);
            this.path.quadTo(this.currentX, this.currentY, x2, y2);
            this.currentX = this.motionTouchEventX;
            this.currentY = this.motionTouchEventY;
            this.extraCanvas.drawPath(this.path, this.paint);
        }

        this.invalidate();
    }

    private void touchUp() {

        sendTouchUpMessage();
        this.path.reset();
    }

    /*method to send messages to the opponent*/

    private void sendTouchStartMessage(float motionTouchEventX, float motionTouchEventY){

        Fragment frag = ((MainActivity) getContext()).getForegroundFragment();
        if(frag instanceof CanvasCurrentPlayer){

            //normalizing coordinates
            float xNorm = motionTouchEventX / extraCanvas.getWidth();
            float yNorm = motionTouchEventY / extraCanvas.getHeight();

            //fragment method to send message
            ((CanvasCurrentPlayer)frag).sendDrawMessage(xNorm, yNorm, -1, -1, MotionEvent.ACTION_DOWN, paint.getColor());
        }
    }

    private void sendTouchMoveMessage(float currentX, float currentY, float x2, float y2){

        Fragment frag = ((MainActivity) getContext()).getForegroundFragment();
        if(frag instanceof CanvasCurrentPlayer){

            //normalizing coordinates
            float xNorm = currentX / extraCanvas.getWidth();
            float yNorm = currentY / extraCanvas.getHeight();
            float x2Norm = x2 / extraCanvas.getWidth();
            float y2Norm = y2 / extraCanvas.getHeight();

            //fragment method to send message
            ((CanvasCurrentPlayer)frag).sendDrawMessage(xNorm, yNorm, x2Norm, y2Norm, MotionEvent.ACTION_MOVE, paint.getColor());
        }
    }

    private void sendTouchUpMessage(){

        Fragment frag = ((MainActivity) getContext()).getForegroundFragment();
        if(frag instanceof CanvasCurrentPlayer){

            //fragment method to send message
            ((CanvasCurrentPlayer)frag).sendDrawMessage(-1, -1, -1, -1, MotionEvent.ACTION_UP, paint.getColor());
        }
    }
}