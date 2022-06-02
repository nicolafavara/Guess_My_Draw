package com.example.guessmydraw.fragments.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.fragments.CanvasOtherPlayer;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;


public class OtherPlayerCanvasView extends View implements NetworkEventCallback {

    private GameViewModel gameViewModel;

    private final Paint paint;
    private final Path path;
    private Canvas extraCanvas;
    private Bitmap extraBitmap;
    private final int backgroundColor;

    private final Receiver receiver;

    private boolean isFirstMessageReceived = true;

    public OtherPlayerCanvasView(Context context, AttributeSet attrs){

        super(context, attrs);
        setSaveEnabled(true);
        this.backgroundColor = ResourcesCompat.getColor(this.getResources(), R.color.page_color, null);
        int drawColor = ResourcesCompat.getColor(this.getResources(), R.color.black, null);
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setColor(drawColor);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(12.0F);

        this.receiver = new Receiver(this);
        this.receiver.start();

        gameViewModel = new ViewModelProvider(((MainActivity) getContext())).get(GameViewModel.class);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (extraBitmap != null) {
            extraBitmap.recycle();
        }

        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        extraCanvas = new Canvas();

        if (gameViewModel.getBitmap() != null){
            extraBitmap = gameViewModel.getBitmap();
            Toast.makeText((MainActivity)getContext(), "Copied", Toast.LENGTH_SHORT).show();
        }
//        extraCanvas.drawColor(backgroundColor);
        extraCanvas.setBitmap(extraBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) { //this canvas is not our this.extraCanvas
        super.onDraw(canvas);
        canvas.drawBitmap(extraBitmap, 0f, 0f, paint);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gameViewModel.setBitmap(extraBitmap);
    }

    @Override
    public void onDrawMessageReceived(DrawMessage message) {

        Log.d("DEBUG", message.toString());

        if (gameViewModel.getIsFirstMsg()){
            Fragment frag = ((MainActivity) getContext()).getForegroundFragment();
            if(frag != null && frag.toString().startsWith(CanvasOtherPlayer.class.getSimpleName())){

                ((CanvasOtherPlayer)frag).firstDrawMessageReceived();
            }
            gameViewModel.setIsFirstMsg(false);
        }

        paint.setColor(message.getPaintColor());

        switch (message.getMotionEventAction()){

            case MotionEvent.ACTION_DOWN:{
                    this.path.reset();
                    float currentX = message.getCurrentX() * extraCanvas.getWidth();
                    float currentY = message.getCurrentY() * extraCanvas.getHeight();
                    this.path.moveTo(currentX, currentY);
                }
                break;

            case MotionEvent.ACTION_MOVE:{
                    //de-normalizing coordinates
                    float currentX = message.getCurrentX() * extraCanvas.getWidth();
                    float currentY = message.getCurrentY() * extraCanvas.getHeight();
                    float x2 = message.getX2() * extraCanvas.getWidth();
                    float y2 = message.getY2() * extraCanvas.getHeight();

                    this.path.quadTo(currentX, currentY, x2, y2);
                    this.extraCanvas.drawPath(this.path, this.paint);

                    postInvalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                this.path.reset();
                break;
        }
    }

    @Override
    public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

    @Override
    public void onWinMessageReceived() {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}

    @Override
    public void onEndingMessageReceived() {/*EMPTY*/}

    @Override
    public void onAckMessageReceived() {/*EMPTY*/}

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) {/*EMPTY*/}

    public interface canvasViewCallback{

        void firstDrawMessageReceived();
    }

}
