package com.chickencoop.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.chickencoop.entities.PlayerActor;
import com.chickencoop.ui.Controller;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class GameplayScreen implements Screen {

    /* Point Lights */
    public static PointLight createPointLight(RayHandler rayHandler, Body body, Color c, float dist) {
        PointLight pl = new PointLight(rayHandler, 120, c, dist, 0, 0);
        pl.setSoftnessLength(0f);
        pl.attachToBody(body);
        pl.setXray(false);
        return pl;
    }
    public static PointLight createPointLight(RayHandler rayHandler, float x, float y, Color c, float dist) {
        PointLight pl = new PointLight(rayHandler, 120, c, dist, x / PPM, y / PPM);
        pl.setSoftnessLength(0f);
        pl.setXray(false);
        return pl;
    }

    /* Cone Lights */
    public static ConeLight createConeLight(RayHandler rayHandler, Body body, Color c, float dist, float dir, float cone) {
        ConeLight cl = new ConeLight(rayHandler, 120, c, dist, 0, 0, dir, cone);
        cl.setSoftnessLength(0f);
        cl.attachToBody(body);
        cl.setXray(false);
        return cl;
    }
    public static ConeLight createConeLight(RayHandler rayHandler, float x, float y, Color c, float dist, float dir, float cone) {
        ConeLight cl = new ConeLight(rayHandler, 120, c, dist, x / PPM, y / PPM, dir, cone);
        cl.setSoftnessLength(0f);
        cl.setXray(false);
        return cl;
    }

    public static final float PPM = 100;
    public static SpriteBatch batch;
    public SpriteBatch stageBatch;
    private RayHandler rayHandler;

    World world;
    float lerp = 0.6f;
    OrthographicCamera cam;
    Viewport viewport;
    Box2DDebugRenderer b2dr;
    Body player;
    Controller controller;
    private Stage stage;
    private Game game;
    private ShapeRenderer shapeRenderer;
    private PlayerActor playerActor;
    private PointLight pl;
    private ConeLight cl;


    public GameplayScreen(Game g) {
        game = g;
        batch = new SpriteBatch();
        stageBatch = new SpriteBatch();
        cam = new OrthographicCamera();
        viewport = new FitViewport(800 / PPM, 480 / PPM, cam);
        stage = new Stage(viewport,stageBatch);
        stage.setDebugAll(true);
        //ca = new ControllerActor("Test");
        playerActor = new PlayerActor();

        //this.stage.addActor(ca);
        this.stage.addActor(playerActor);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, 0), true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.5f);
        b2dr = new Box2DDebugRenderer();
        createGround();
        createPlayer();
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(stage);
        controller = new Controller(batch,m);
        Gdx.input.setInputProcessor(m);
        pl = createPointLight(rayHandler, player, Color.WHITE,10);

    }

    @Override
    public void render(float delta) {
        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        controller.draw();
        stage.draw();
        rayHandler.update();
        batch.setProjectionMatrix(cam.combined);
        rayHandler.setCombinedMatrix(cam);

        b2dr.render(world, cam.combined);
        rayHandler.render();


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        controller.resize(width, height);
    }

    public void handleInput() {
        player.setLinearVelocity(controller.speed());
        playerActor.setSpeed(player.getLinearVelocity());
    }

    public void update(float dt) {
        handleInput();
        rayHandler.update();
        world.step(dt, 6, 2);
        Vector3 position = cam.position;
        Vector2 playerRbPos = player.getPosition();
        position.x += (playerRbPos.x - position.x) * lerp * dt;
        position.y += (playerRbPos.y - position.y) * lerp * dt;
        playerActor.setPosition(playerRbPos.x,playerRbPos.y);

        cam.update();
    }


    public void createGround() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(viewport.getWorldWidth() / 2, 0);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(viewport.getWorldWidth() / 2, 20 / PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }

    public void createPlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(0,0);
        bdef.type = BodyDef.BodyType.DynamicBody;
        player = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(40 / PPM, 40 / PPM);
        playerActor.setWidth(40 / PPM);
        playerActor.setHeight(40 / PPM);
        playerActor.setColor(Color.BLACK);
        playerActor.debug();
        playerActor.toFront();


        fdef.shape = shape;
        player.createFixture(fdef);
        player.setLinearDamping(0.01f);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
