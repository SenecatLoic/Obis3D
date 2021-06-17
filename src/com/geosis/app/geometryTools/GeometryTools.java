package com.geosis.app.geometryTools;

import com.geosis.api.object.ZoneSpecies;
import com.geosis.app.controlTools.Legend;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;

import java.util.List;

/**
 * Regroupe les outils géomtriques
 */

public class GeometryTools {

    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    private static final float TEXTURE_OFFSET = 1.01f;

    private static float[] heightsBox = {0.15f, 0.3f, 0.45f, 0.60f, 0.75f, 0.90f, 1.05f , 1.2f};
    private static Box barreHistogramme;

    /**
     * Convertir des Coordonnées (lat, lon) en Point3D (méthode du tutoriel)
     * @param lat
     * @param lon
     * @return Point3D créé à partir de latitude et longitude
     */
    public static Point3D geoCoordTo3dCoord(float lat, float lon) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)),
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)),
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)));
    }

    /**
     * Convertir des Coordonnées 3D en (lat, lon) (méthode du tutoriel)
     * @param p
     * @return Point2D (X = latitude ; Y = longitude)
     */
    public static Point2D spaceCoordToGeoCoord(Point3D p) {
        float lat = (float) (Math.asin(-p.getY() / TEXTURE_OFFSET)
                * (180 / Math.PI) - TEXTURE_LAT_OFFSET);
        float lon;
        if (p.getZ() < 0) {
            lon = 180 - (float) (Math.asin(-p.getX() / (TEXTURE_OFFSET * Math.cos((Math.PI / 180)
                    * (lat + TEXTURE_LAT_OFFSET)))) * 180 / Math.PI + TEXTURE_LON_OFFSET);
        } else {
            lon = (float) (Math.asin(-p.getX() / (TEXTURE_OFFSET * Math.cos((Math.PI / 180)
                    * (lat + TEXTURE_LAT_OFFSET)))) * 180 / Math.PI - TEXTURE_LON_OFFSET);
        }
        return new Point2D(lat, lon);
    }

    /**
     * Crée un polygone grâce aux 5 points de chaque Zone
     * @param parent
     * @param coords
     * @param color
     */
    public static void addPolygon(Group parent, Point2D[] coords, Color color){

        final TriangleMesh triangleMesh = new TriangleMesh();

        Point3D coord1 = geoCoordTo3dCoord((float)coords[0].getY(), (float)coords[0].getX());
        Point3D coord2 = geoCoordTo3dCoord((float)coords[1].getY(), (float)coords[1].getX());
        Point3D coord3 = geoCoordTo3dCoord((float)coords[2].getY(), (float)coords[2].getX());
        Point3D coord4 = geoCoordTo3dCoord((float)coords[3].getY(), (float)coords[3].getX());
        Point3D coord5 = geoCoordTo3dCoord((float)coords[4].getY(), (float)coords[4].getX());

        final float[] points = {
                (float)coord1.getX(), (float) coord1.getY(), (float)coord1.getZ(),
                (float)coord2.getX(), (float) coord2.getY(), (float)coord2.getZ(),
                (float)coord3.getX(), (float) coord3.getY(), (float)coord3.getZ(),
                (float)coord4.getX(), (float) coord4.getY(), (float)coord4.getZ(),
                (float)coord5.getX(), (float) coord5.getY(), (float)coord5.getZ(),
        };

        final float[] texCoords = {
                1, 1,
                1, 0,
                1, -1,
                0, 1,
                0, 0
        };

        final int[] faces = {
                0, 0, 1, 1, 2, 2,
                0, 0, 2, 2, 3, 3,
                0, 0, 3, 3, 4, 4
        };

        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);

        // TODO couleur translucide
        Color colorTrans = Color.color(color.getRed(), color.getGreen(), color.getBlue(), 1);

        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(colorTrans);

        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        // permet de voir les faces avant et arrière des formes
        meshView.setCullFace(CullFace.NONE);
        parent.getChildren().addAll(meshView);
    }

    /**
     * Crée une barre dé l'histogramme 3D au milieu de chaque polygon
     * @see #lookAt(Point3D, Point3D, Point3D) 
     * @param parent
     * @param from2D
     * @param height
     * @param color
     */
    public static void addBoxHistogramme(Group parent, Point2D[] from2D, float height, Color color){

        barreHistogramme = new Box(0.01f,0.01f,height);

        Color colorTrans = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.3);

        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(colorTrans);
        barreHistogramme.setMaterial(material);

        // Calcul du barycentre du polygone
        double x = 0;
        double y = 0;

        for (Point2D point : from2D) {
            x += point.getX();
            y += point.getY();
        }

        x = (double) x / from2D.length;
        y = (double) y / from2D.length;

        Point3D from = geoCoordTo3dCoord((float) y, (float) x);
        Point3D to = Point3D.ZERO;
        Point3D yDir = new Point3D(0, 1, 0);

        Group group = new Group();
        Affine affine = new Affine();
        affine.append( lookAt(from,to,yDir));
        group.getTransforms().setAll(affine);
        group.getChildren().addAll(barreHistogramme);

        parent.getChildren().addAll(group);

    }

    /**
     * Détermine la hauteur de la box selon sa couleur
     * @see #addPolygon(Group, Point2D[], Color) 
     * @param zoneSpecies
     * @param minNbSignals
     * @param maxNbSignals
     * @param colorsPane
     * @return float Height d'une box
     */
    public static float getHeightBox(ZoneSpecies zoneSpecies, int minNbSignals, int maxNbSignals, List<Rectangle > colorsPane){

        Color colorBox = Legend.getColor(zoneSpecies, minNbSignals, maxNbSignals, colorsPane);

        for(int i = 0; i < heightsBox.length; i++){
            if(colorBox.equals((Color) colorsPane.get(i).getFill())) {
                return heightsBox[i];
            }
        }
        return 0;

    }

    /**
     * Crée l'axe selon lequel on veut créer l'objet
     * @param from
     * @param to
     * @param ydir
     * @return Affine
     */
    public static Affine lookAt(Point3D from, Point3D to, Point3D ydir) {
        Point3D zVec = to.subtract(from).normalize();
        Point3D xVec = ydir.normalize().crossProduct(zVec).normalize();
        Point3D yVec = zVec.crossProduct(xVec).normalize();
        return new Affine(xVec.getX(), yVec.getX(), zVec.getX(), from.getX(),
                xVec.getY(), yVec.getY(), zVec.getY(), from.getY(),
                xVec.getZ(), yVec.getZ(), zVec.getZ(), from.getZ());
    }

}
