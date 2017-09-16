package client;

import client.graphics.Hud;
import client.graphics.Blackout;
import nightingale.Application;
import nightingale.data.DataEngine;
import nightingale.gui.*;
import nightingale.structures.Texture;
import org.lwjgl.glfw.GLFW;
import scenarios.*;
import sprites.Actor;
import world.World;

/**
 * Created by Aunmag on 2016.11.09.
 */

public class GamePlay extends Application {

    private static boolean isPause = true;
    private static Scenario scenario;
    private static World world;
    private static GuiButtonBack buttonContinue;

    public GamePlay() {
        scenario = new ScenarioEncircling();
        Actor.loadSounds();
        buttonContinue = new GuiButtonBack(4, 7, 4, 1, "Continue");
        buttonContinue.setIsAvailable(false);
        initializePages();
    }

    private void initializeWorld() {
        deleteWorld();
        world = new World();
        buttonContinue.setIsAvailable(true);
    }

    private void initializePages() {
        initializePageMain();
    }

    private void initializePageMain() {
        GuiLabel[] labels = new GuiLabel[] {
                new GuiLabel(3, 3, 6, 1, DataEngine.name),
                new GuiLabel(5, 4, 2, 1, String.format(
                        "v%s by %s", DataEngine.version, DataEngine.developer
                )),
        };

        GuiButton[] buttons = new GuiButton[] {
//                Runnable action = () -> {
//                    sound.stop();
//                };
                buttonContinue,
                new GuiButtonAction(4, 8, 4, 1, "New game", () -> {
                    initializeWorld();
                    setPause(false);
                }),
                new GuiButtonLink(4, 9, 4, 1, "Help", createPageHelp()),
                new GuiButtonLink(4, 10, 4, 1, "Exit", createPageExit()),
        };

        Texture wallpaper = Texture.getOrCreate("images/wallpapers/main_menu");
        wallpaper.scaleAsWallpaper();

        new GuiPage(labels, buttons, wallpaper).open();
    }

    private GuiPage createPageHelp() {
        GuiLabel[] labels = new GuiLabel[] {
                new GuiLabel(5, 1, 2, 1, "Help"),

                new GuiLabel(4, 3, 1, 1, "Movement"),
                new GuiLabel(7, 3, 1, 1, "W, A, S, D"),

                new GuiLabel(4, 4, 1, 1, "Rotation"),
                new GuiLabel(7, 4, 1, 1, "Mouse"),

                new GuiLabel(4, 5, 1, 1, "Sprint"),
                new GuiLabel(7, 5, 1, 1, "Shift"),

                new GuiLabel(4, 6, 1, 1, "Attack"),
                new GuiLabel(7, 6, 1, 1, "LMB"),

                new GuiLabel(4, 7, 1, 1, "Zoom in/out"),
                new GuiLabel(7, 7, 1, 1, "+/- or Wheel Up/Down"),

                new GuiLabel(4, 8, 1, 1, "Menu"),
                new GuiLabel(7, 8, 1, 1, "Escape"),
        };


        GuiButton[] buttons = new GuiButton[] {
            new GuiButtonBack(4, 10, 4, 1, "Back"),
        };

        Texture wallpaper = Texture.getOrCreate("images/wallpapers/help");
        wallpaper.scaleAsWallpaper();

        return new GuiPage(labels, buttons, wallpaper);
    }

    private GuiPage createPageExit() {
        GuiLabel[] labels = new GuiLabel[] {
                new GuiLabel(3, 5, 6, 1, "Are you sure you want to exit?"),
        };

        Runnable actionExit = () -> Application.isRunning = false;
        GuiButton[] buttons = new GuiButton[] {
                new GuiButtonBack(3, 9, 3, 1, "No"),
                new GuiButtonAction(6, 9, 3, 1, "Yes", actionExit),
        };

        Texture wallpaper = Texture.getOrCreate("images/wallpapers/exit");
        wallpaper.scaleAsWallpaper();

        return new GuiPage(labels, buttons, wallpaper);
    }

    protected void gameUpdate() {
        if (isPause) {
            GuiManager.update();
            if (GuiManager.isShouldClose() && isWorldCreated()) {
                setPause(false);
            }
        } else {
            updateInputForCamera();
            updateInputForPlayer();
            world.update();
            scenario.update();
            if (Application.getInput().isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                setPause(true);
            }
        }
    }

    private void updateInputForCamera() {
        float zoom = Application.getCamera().getZoom();
        float zoomChange = zoom * 0.01f;

        if (Application.getInput().isKeyDown(GLFW.GLFW_KEY_KP_ADD)) {
            Application.getCamera().setZoom(zoom + zoomChange);
        } else if (Application.getInput().isKeyDown(GLFW.GLFW_KEY_KP_SUBTRACT)) {
            Application.getCamera().setZoom(zoom - zoomChange);
        }
    }

    private void updateInputForPlayer() {
        Actor player = Actor.getPlayer();

        if (player == null) {
            return;
        }

        float mouseVelocityX = Application.getInput().getMouseVelocity().x;
        player.addRadiansCarefully(mouseVelocityX * 0.005f);

        player.isWalkingForward = Application.getInput().isKeyDown(GLFW.GLFW_KEY_W);
        player.isWalkingBack = Application.getInput().isKeyDown(GLFW.GLFW_KEY_S);
        player.isWalkingLeft = Application.getInput().isKeyDown(GLFW.GLFW_KEY_A);
        player.isWalkingRight = Application.getInput().isKeyDown(GLFW.GLFW_KEY_D);
        player.isSprinting = Application.getInput().isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT);
        player.isAttacking = Application.getInput().isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_1);
    }

    protected void gameRender() {
        if (isPause) {
            GuiManager.render();
        } else {
            world.render();
            Blackout.render();
            scenario.render();
            Hud.render();
        }
    }

    protected void gameTerminate() {
        deleteWorld();
//        scenario = null;
    }

    public static void deleteWorld() {
        if (isWorldCreated()) {
            world.remove();
            world = null;
        }

        buttonContinue.setIsAvailable(false);
    }

    /* Setters */

    public static void setPause(boolean isPause) {
        GamePlay.isPause = isPause;
        Application.getWindow().setCursorGrabbed(!isPause);

        if (isPause) {
            GuiManager.activate();

        }

        if (isWorldCreated()) {
            if (!isPause) {
                world.play();
            } else {
                world.stop();
            }
        }
    }

    /* Getters */

    public static boolean isPause() {
        return isPause;
    }

    public static World getWorld() {
        return world;
    }

    public static boolean isWorldCreated() {
        return world != null;
    }

}
