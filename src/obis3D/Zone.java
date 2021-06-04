package obis3D;

import java.awt.*;

public class Zone {

    private Point coord1;
    private Point coord2;
    private Point coord3;
    private Point coord4;
    private Point coord5;

    public Zone(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int x5, int y5){
        this.coord1 = new Point(x1, y1);
        this.coord2 = new Point(x2, y2);
        this.coord3 = new Point(x3, y3);
        this.coord4 = new Point(x4, y4);
        this.coord5 = new Point(x5, y5);
    }

    public Point getCoord1() {
        return this.coord1;
    }

    public Point getCoord2() {
        return this.coord2;
    }

    public Point getCoord3() {
        return this.coord3;
    }

    public Point getCoord4() {
        return this.coord4;
    }

    public Point getCoord5() {
        return this.coord5;
    }



}
