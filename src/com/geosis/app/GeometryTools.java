package com.geosis.app;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class GeometryTools {

    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    private static final float TEXTURE_OFFSET = 1.01f;

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

        Point3D coord1 = geoCoordTo3dCoord((float)coords[0].getX(), (float)coords[0].getY());
        Point3D coord2 = geoCoordTo3dCoord((float)coords[1].getX(), (float)coords[1].getY());
        Point3D coord3 = geoCoordTo3dCoord((float)coords[2].getX(), (float)coords[2].getY());
        Point3D coord4 = geoCoordTo3dCoord((float)coords[3].getX(), (float)coords[3].getY());
        Point3D coord5 = geoCoordTo3dCoord((float)coords[4].getX(), (float)coords[4].getY());

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

        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);

        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        // permet de voir les faces avant et arrière des formes
        meshView.setCullFace(CullFace.NONE);
        parent.getChildren().addAll(meshView);
    }
}
