package aunmag.shooter.client.graphics;

import aunmag.nightingale.Application;
import aunmag.nightingale.utilities.UtilsGraphics;
import aunmag.nightingale.utilities.UtilsMath;
import aunmag.shooter.environment.actor.Actor;
import org.lwjgl.opengl.GL11;

public class Crosshair {

    private static final int size = 5;
    private Actor shooter;

    public Crosshair(Actor shooter) {
        this.shooter = shooter;
    }

    public void render() {
        if (shooter.isAiming.isCompletelyOff()) {
            return;
        }

        float scale = Application.getCamera().getScaleFull();
        float degree = shooter.isAiming.getCurrent();
        float radians = shooter.body.radians;

        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float distance = Application.getWindow().getCenterY() * degree / scale;
        float x = shooter.body.position.x + (distance + shooter.body.radius) * cos;
        float y = shooter.body.position.y + (distance + shooter.body.radius) * sin;

        float offset = size / scale;
        float offsetX1 = offset * (float) Math.cos(radians + UtilsMath.PIx0_5);
        float offsetY1 = offset * (float) Math.sin(radians + UtilsMath.PIx0_5);
        float offsetX2 = offsetX1 * 3;
        float offsetY2 = offsetY1 * 3;

        float alpha = UtilsMath.limitNumber(distance, 0, 1);
        GL11.glColor4f(1f, 1f, 1f, alpha);
        UtilsGraphics.drawLine(x + offsetX1, y + offsetY1, x + offsetX2, y + offsetY2, true);
        UtilsGraphics.drawLine(x - offsetX1, y - offsetY1, x - offsetX2, y - offsetY2, true);

        GL11.glLineStipple(size, (short) 0xAAAA);
        GL11.glEnable(GL11.GL_LINE_STIPPLE);
        float x2 = x - distance * cos;
        float y2 = y - distance * sin;
        UtilsGraphics.drawLine(x, y, x2, y2, true);
        GL11.glDisable(GL11.GL_LINE_STIPPLE);
    }

}
