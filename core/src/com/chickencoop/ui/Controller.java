package com.chickencoop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Controller extends Actor {
    static float threshold = 0.1f;
    Viewport viewport;
    Stage stage;
    boolean upPressed=false;
    Vector2 initialTouchPos;
    Vector2 currentPosition;
    Vector2 joystickCenter;
    OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;
    private Vector2 zeroSpeed;


    public Controller(SpriteBatch b){
        InputMultiplexer m = new InputMultiplexer();
        init(b,m);
    }
    public Controller(SpriteBatch b, InputMultiplexer m){
        init(b,m);
    }
    private void init(SpriteBatch b, InputMultiplexer m){


        zeroSpeed = currentPosition = initialTouchPos = new Vector2(0,0);

        cam = new OrthographicCamera();
        viewport = new FitViewport(800, 480, cam);
        stage = new Stage(viewport, b);

        m.addProcessor(stage);

        Table table = new Table();
        table.left().bottom();

        Image joystick_image = new Image(new Texture("joystick.png"));
        joystick_image.setSize(150, 150);
        shapeRenderer = new ShapeRenderer();
        joystickCenter = new Vector2(75,75);
        joystick_image.addListener(new InputListener() {

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                currentPosition = new Vector2(x,y);
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                initialTouchPos = new Vector2(x,y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        stage.addActor(joystick_image);
     }

    public void draw(){
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glLineWidth(30);
        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();
        if(touchStarted()){
            shapeRenderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
            shapeRenderer.setTransformMatrix(stage.getBatch().getTransformMatrix());
            shapeRenderer.translate(currentPosition.x,currentPosition.y, 0);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.circle(0,0,20);
            shapeRenderer.end();
        }

        Gdx.gl.glLineWidth(2);


    }


    public boolean touchStarted(){
        return upPressed;
    }
    public Vector2 speed(){
        Vector2 speed = initialTouchPos.cpy().sub(currentPosition).nor().scl(-1);
        if(upPressed && speed.len2() > threshold*threshold){
            return speed;
        }
        return zeroSpeed;
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }
}