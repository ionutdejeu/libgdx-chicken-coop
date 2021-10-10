package com.chickencoop.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;

public class PlayerActor extends Actor {

    Sprite s;
    TextureAtlas atlas;
    // A variable for tracking elapsed time for the animation
    float stateTime;
    ShapeRenderer shape;
    Vector2 speed;
    HashMap<Vector2,Animation<TextureRegion>> playerAnimMap;

    public void setSpeed(Vector2 dir,float scale){
        setSpeed(dir.cpy().scl(scale));
    }
    public void setSpeed(Vector2 speed){
        this.speed = speed;
    }
    /** Called when the actor's position has been changed. */
    protected void positionChanged () {
        s.setPosition(getX(),getY());
        super.positionChanged();
    }

    public PlayerActor(){
        speed = Vector2.Zero;
        s = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        this.setBounds(getX(),getY(),10f,10f);

        atlas = new TextureAtlas(Gdx.files.internal("characters/generic/generic_character.atlas"));

        Animation<TextureRegion> walk_left =new Animation<TextureRegion>(0.25f,
                atlas.findRegions("walk_left"), Animation.PlayMode.LOOP);

        Animation<TextureRegion> walk_right =new Animation<TextureRegion>(0.25f,
                atlas.findRegions("walk_right"), Animation.PlayMode.LOOP);

        Animation<TextureRegion> walk_top =new Animation<TextureRegion>(0.25f,
                atlas.findRegions("walk_top"), Animation.PlayMode.LOOP);

        Animation<TextureRegion> walk_down =new Animation<TextureRegion>(0.25f,
                atlas.findRegions("walk_down"), Animation.PlayMode.LOOP);

        Animation<TextureRegion> idle =new Animation<TextureRegion>(0.25f,
                atlas.findRegions("idle"), Animation.PlayMode.LOOP);

        stateTime = 0f;
        playerAnimMap = new HashMap<>();
        playerAnimMap.put(new Vector2(-1,0),walk_right);
        playerAnimMap.put(new Vector2(0,1),walk_down);
        playerAnimMap.put(new Vector2(1,0),walk_left);
        playerAnimMap.put(new Vector2(0,-1),walk_top);
        playerAnimMap.put(new Vector2(0,0),idle);

        shape = new ShapeRenderer();

    }
    private Animation<TextureRegion> getAnimation(){
        Vector2 speedDir = Vector2.Zero;
        float dot = 0f;
        for(Vector2 dir: playerAnimMap.keySet()){
            float speedDot = speed.dot(dir);
            if(speedDot < dot){
                dot = speedDot;
                speedDir = dir;
            }
        }
        return playerAnimMap.get(speedDir);
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame = getAnimation().getKeyFrame(stateTime, true);

        //batch.draw(currentFrame, this.getX(),this.getY());
        batch.draw(currentFrame, this.getX(),this.getY(), 0, 0, this.getWidth(), this.getHeight(), 1f, 1f, 0f);

        //batch.draw(s,this.getX(),this.getY());


    }
}
