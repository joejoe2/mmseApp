package com.joejoe2.surveyapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;


public class DrawingView extends View {
    private Paint mPaint, mPaintFinal;
    private Canvas canvas;
    private Bitmap bitmap;
    private float startX, startY, currentX, currentY, endX, endY;
    private boolean isDrawing;
    private Graph<CanvasVertex, DefaultEdge> graph;
    private CanvasVertex startVertex, endVertex;
    private static final float TOUCH_TOLERANCE = 10;
    private static final float TOUCH_STROKE_WIDTH = 5;
    private static final float AUTO_COMPLETE_RANGE = 120;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);

        mPaintFinal = new Paint(Paint.DITHER_FLAG);
        mPaintFinal.setAntiAlias(true);
        mPaintFinal.setDither(true);
        mPaintFinal.setColor(Color.WHITE);
        mPaintFinal.setStyle(Paint.Style.STROKE);
        mPaintFinal.setStrokeJoin(Paint.Join.ROUND);
        mPaintFinal.setStrokeCap(Paint.Cap.ROUND);
        mPaintFinal.setStrokeWidth(TOUCH_STROKE_WIDTH);

        graph = buildEmptySimpleGraph();
    }

    public void reset(){
        graph=buildEmptySimpleGraph();
        startVertex=null;
        endVertex=null;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
    }

    private Graph<CanvasVertex, DefaultEdge> buildEmptySimpleGraph(){
        return GraphTypeBuilder
                .<CanvasVertex, DefaultEdge> undirected().allowingMultipleEdges(false)
                .allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
    }

    public Graph<CanvasVertex, DefaultEdge> getDrawing(){
        return graph;
    }

    private void logGraph(){
        System.out.println(startVertex);
        System.out.println(endVertex);
        for (CanvasVertex vertex:graph.vertexSet()){
            System.out.println(vertex);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas=new Canvas(bitmap);
    }

    /**
     * preview during drawing path when finger is holding on the canvas
     * @param canvas
     */
    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        if (isDrawing){
            if (isFarEnough(currentX - startX, currentY - startY, TOUCH_TOLERANCE)) {
                CanvasVertex closest=findClosestVertexInGraph(currentX, currentY);
                //if current pos is close enough to any vertex in graph,
                //let the preview line connect to the closet vertex
                if (graph.vertexSet().size()!=0&&disBetween(currentX, currentY, closest)<AUTO_COMPLETE_RANGE){
                    canvas.drawLine(startX, startY, closest.getX(), closest.getY(), mPaint);
                }else {
                    canvas.drawLine(startX, startY, currentX, currentY, mPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMultiTouch(event)) {
            return false;
        }
        //update pos
        currentX = event.getX();
        currentY = event.getY();
        //decide start and finish to draw
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDrawingByFinger();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                releaseDrawingByFinger();
                break;
            default:
                return false;
        }
        //update canvas
        invalidate();
        return true;
    }

    private boolean isMultiTouch(MotionEvent event){
        return event.getPointerCount()!=1;
    }

    private void startDrawingByFinger(){
        isDrawing=true;
        CanvasVertex closest=findClosestVertexInGraph(currentX, currentY);
        //decide start point of drawing
        if (graph.vertexSet().size()!=0&&disBetween(currentX, currentY, closest)<AUTO_COMPLETE_RANGE){
            startX=closest.getX();
            startY=closest.getY();
        }else {
            startX = currentX;
            startY = currentY;
        }
    }

    private void releaseDrawingByFinger(){
        isDrawing=false;
        if (isFarEnough(currentX - startX, currentY - startY, TOUCH_TOLERANCE)) {
            //add start point of drawing to graph and canvas
            startVertex=addPointToGraph(startX, startY);
            canvas.drawCircle(startX, startY, TOUCH_STROKE_WIDTH, mPaintFinal);
            //decide end point of drawing
            CanvasVertex closest = findClosestVertexInGraph(currentX, currentY);
            if (graph.vertexSet().size()!=0&&disBetween(currentX, currentY, closest)<AUTO_COMPLETE_RANGE){
                endX=closest.getX();
                endY=closest.getY();
            }else {
                endX=currentX;
                endY=currentY;
            }
            //add end point of drawing to graph and canvas
            endVertex=addPointToGraph(endX, endY);
            if (!startVertex.equals(endVertex)&&!graph.containsEdge(startVertex, endVertex))graph.addEdge(startVertex, endVertex);
            canvas.drawCircle(endX, endY, TOUCH_STROKE_WIDTH, mPaintFinal);
            //draw line from start to end
            canvas.drawLine(startX, startY, endX, endY, mPaintFinal);
        }
    }

    private CanvasVertex addPointToGraph(float x, float y){
        CanvasVertex vertex=new CanvasVertex(x, y, graph.vertexSet().size());
        if(!graph.containsVertex(vertex))graph.addVertex(vertex);
        return vertex;
    }

    private CanvasVertex findClosestVertexInGraph(float x, float y) {
        CanvasVertex res=null;
        float minDistance=Float.MAX_VALUE;
        for (CanvasVertex vertex:graph.vertexSet()){
            float distance= disBetween(x, y, vertex);
            if (distance<minDistance){
                minDistance=distance;
                res=vertex;
            }
        }
        return res;
    }

    private float disBetween(float x, float y, CanvasVertex vertex){
        return (float) Math.sqrt((x-vertex.getX())*(x-vertex.getX())+(y-vertex.getY())*(y-vertex.getY()));
    }

    private boolean isFarEnough(float dx, float dy, float threshold){
        return Math.abs(dx)>= threshold || Math.abs(dy) >= threshold;
    }
}
