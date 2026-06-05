package tiled;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

// Modified version of:
// com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
// with added ColorSetter/apply
public class TiledMapRenderer {

  public interface ColorSetter {

    public float apply(Color color, float x, float y);
  }

	public static final int NUM_VERTICES = 20;
	public static final float[] vertices = new float[NUM_VERTICES];

	public static void renderTileLayer (TiledMapTileLayer layer, Batch batch, float unitScale, Rectangle viewBounds, ColorSetter colorSetter) {
		final Color batchColor = batch.getColor();

		final int layerWidth = layer.getWidth();
		final int layerHeight = layer.getHeight();

		final float layerTileWidth = layer.getTileWidth() * unitScale;
		final float layerTileHeight = layer.getTileHeight() * unitScale;

		final float layerOffsetX = layer.getRenderOffsetX() * unitScale;
		// offset in tiled is y down, so we flip it
		final float layerOffsetY = -layer.getRenderOffsetY() * unitScale;

		final int col1 = Math.max(0, (int)((viewBounds.x - layerOffsetX) / layerTileWidth));
		final int col2 = Math.min(layerWidth,
			(int)((viewBounds.x + viewBounds.width + layerTileWidth - layerOffsetX) / layerTileWidth));

		final int row1 = Math.max(0, (int)((viewBounds.y - layerOffsetY) / layerTileHeight));
		final int row2 = Math.min(layerHeight,
			(int)((viewBounds.y + viewBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight));

		float y = row2 * layerTileHeight + layerOffsetY;
		float xStart = col1 * layerTileWidth + layerOffsetX;
		final float[] vertices = TiledMapRenderer.vertices.clone();

		for (int row = row2; row >= row1; row--) {
			float x = xStart;
			for (int col = col1; col < col2; col++) {
				final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
				if (cell == null) {
					x += layerTileWidth;
					continue;
				}
				final TiledMapTile tile = cell.getTile();

				if (tile != null) {
					final boolean flipX = cell.getFlipHorizontally();
					final boolean flipY = cell.getFlipVertically();
					final int rotations = cell.getRotation();

					TextureRegion region = tile.getTextureRegion();

					float x1 = x + tile.getOffsetX() * unitScale;
					float y1 = y + tile.getOffsetY() * unitScale;
					float x2 = x1 + region.getRegionWidth() * unitScale;
					float y2 = y1 + region.getRegionHeight() * unitScale;

					float u1 = region.getU();
					float v1 = region.getV2();
					float u2 = region.getU2();
					float v2 = region.getV();

          // System.out.println("drawing tile : " + col + "," + row);
          // System.out.println("x1 : " + x1 + " , x2:" + x2);
          // System.out.println("y1 : " + y1 + " , y2:" + y2);

          float color11 = colorSetter.apply(batchColor, x1, y1);
          float color12 = colorSetter.apply(batchColor, x1, y2);
          float color22 = colorSetter.apply(batchColor, x2, y2);
          float color21 = colorSetter.apply(batchColor, x2, y1);

          // float color11 = Color.WHITE.toFloatBits();
          // float color12 = Color.WHITE.toFloatBits();
          // float color22 = Color.BLACK.toFloatBits();
          // float color21 = Color.BLACK.toFloatBits();

					vertices[X1] = x1;
					vertices[Y1] = y1;
					vertices[C1] = color11;
					vertices[U1] = u1;
					vertices[V1] = v1;

					vertices[X2] = x1;
					vertices[Y2] = y2;
					vertices[C2] = color12;
					vertices[U2] = u1;
					vertices[V2] = v2;

					vertices[X3] = x2;
					vertices[Y3] = y2;
					vertices[C3] = color22;
					vertices[U3] = u2;
					vertices[V3] = v2;

					vertices[X4] = x2;
					vertices[Y4] = y1;
					vertices[C4] = color21;
					vertices[U4] = u2;
					vertices[V4] = v1;

					if (flipX) {
						float temp = vertices[U1];
						vertices[U1] = vertices[U3];
						vertices[U3] = temp;
						temp = vertices[U2];
						vertices[U2] = vertices[U4];
						vertices[U4] = temp;
					}
					if (flipY) {
						float temp = vertices[V1];
						vertices[V1] = vertices[V3];
						vertices[V3] = temp;
						temp = vertices[V2];
						vertices[V2] = vertices[V4];
						vertices[V4] = temp;
					}
					if (rotations != 0) {
						switch (rotations) {
						case Cell.ROTATE_90: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V2];
							vertices[V2] = vertices[V3];
							vertices[V3] = vertices[V4];
							vertices[V4] = tempV;

							float tempU = vertices[U1];
							vertices[U1] = vertices[U2];
							vertices[U2] = vertices[U3];
							vertices[U3] = vertices[U4];
							vertices[U4] = tempU;
							break;
						}
						case Cell.ROTATE_180: {
							float tempU = vertices[U1];
							vertices[U1] = vertices[U3];
							vertices[U3] = tempU;
							tempU = vertices[U2];
							vertices[U2] = vertices[U4];
							vertices[U4] = tempU;
							float tempV = vertices[V1];
							vertices[V1] = vertices[V3];
							vertices[V3] = tempV;
							tempV = vertices[V2];
							vertices[V2] = vertices[V4];
							vertices[V4] = tempV;
							break;
						}
						case Cell.ROTATE_270: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V4];
							vertices[V4] = vertices[V3];
							vertices[V3] = vertices[V2];
							vertices[V2] = tempV;

							float tempU = vertices[U1];
							vertices[U1] = vertices[U4];
							vertices[U4] = vertices[U3];
							vertices[U3] = vertices[U2];
							vertices[U2] = tempU;
							break;
						}
						}
					}
					batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
				}
				x += layerTileWidth;
			}
			y -= layerTileHeight;
		}
	}

	public static void render (TiledMap map, float unitScale, Rectangle viewBounds, OrthographicCamera camera, Batch batch, ColorSetter colorSetter, int[] layers) {
		float width = camera.viewportWidth * camera.zoom;
		float height = camera.viewportHeight * camera.zoom;
		float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
		float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
		viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
		for (int layerIdx : layers) {
			MapLayer layer = map.getLayers().get(layerIdx);
			renderMapLayer(layer, batch, unitScale, viewBounds, colorSetter);
		}
	}

	public static void renderMapLayer (MapLayer layer, Batch batch, float unitScale, Rectangle viewBounds, ColorSetter colorSetter) {
		if (!layer.isVisible()) return;
		if (layer instanceof MapGroupLayer) {
			MapLayers childLayers = ((MapGroupLayer)layer).getLayers();
			for (int i = 0; i < childLayers.size(); i++) {
				MapLayer childLayer = childLayers.get(i);
				if (!childLayer.isVisible()) continue;
				renderMapLayer(childLayer, batch, unitScale, viewBounds, colorSetter);
			}
		} else {
			if (layer instanceof TiledMapTileLayer) {
				renderTileLayer((TiledMapTileLayer)layer, batch, unitScale, viewBounds, colorSetter);
			}
		}
	}
}
