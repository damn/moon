package moon;

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
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class TiledMapRenderer implements Disposable {

  public interface ColorSetter {

    public float apply(Color color, float x, float y);
  }

	static protected final int NUM_VERTICES = 20;
	protected TiledMap map;
	protected float unitScale;
	protected Batch batch;
	protected Rectangle viewBounds;
	protected Rectangle imageBounds = new Rectangle();
	protected Rectangle repeatedImageBounds = new Rectangle();
	protected boolean ownsBatch;
	protected float[] vertices = new float[NUM_VERTICES];
	private ColorSetter colorSetter;

	public TiledMapRenderer (TiledMap map, float unitScale, Batch batch) {
		this.map = map;
		this.unitScale = unitScale;
		this.viewBounds = new Rectangle();
		this.batch = batch;
		this.ownsBatch = false;
	}

  public void setColorSetter(ColorSetter colorSetter) {
    this.colorSetter = colorSetter;
  }

	public void renderTileLayer (TiledMapTileLayer layer) {
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
		final float[] vertices = this.vertices;

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

	public TiledMap getMap () {
		return map;
	}

	public void setMap (TiledMap map) {
		this.map = map;
	}

	public float getUnitScale () {
		return unitScale;
	}

	public Batch getBatch () {
		return batch;
	}

	public Rectangle getViewBounds () {
		return viewBounds;
	}

	public void setView (OrthographicCamera camera) {
		batch.setProjectionMatrix(camera.combined);
		float width = camera.viewportWidth * camera.zoom;
		float height = camera.viewportHeight * camera.zoom;
		float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
		float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
		viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
	}

	public void setView (Matrix4 projection, float x, float y, float width, float height) {
		batch.setProjectionMatrix(projection);
		viewBounds.set(x, y, width, height);
	}

	public void render () {
		beginRender();
		for (MapLayer layer : map.getLayers()) {
			renderMapLayer(layer);
		}
		endRender();
	}

	public void render (int[] layers) {
		beginRender();
		for (int layerIdx : layers) {
			MapLayer layer = map.getLayers().get(layerIdx);
			renderMapLayer(layer);
		}
		endRender();
	}

	public void renderMapLayer (MapLayer layer) {
		if (!layer.isVisible()) return;
		if (layer instanceof MapGroupLayer) {
			MapLayers childLayers = ((MapGroupLayer)layer).getLayers();
			for (int i = 0; i < childLayers.size(); i++) {
				MapLayer childLayer = childLayers.get(i);
				if (!childLayer.isVisible()) continue;
				renderMapLayer(childLayer);
			}
		} else {
			if (layer instanceof TiledMapTileLayer) {
				renderTileLayer((TiledMapTileLayer)layer);
			} else if (layer instanceof TiledMapImageLayer) {
				renderImageLayer((TiledMapImageLayer)layer);
			} else {
				renderObjects(layer);
			}
		}
	}

	public void renderObjects (MapLayer layer) {
		for (MapObject object : layer.getObjects()) {
			renderObject(object);
		}
	}

	public void renderObject (MapObject object) {

	}

	public void renderImageLayer (TiledMapImageLayer layer) {
		final Color batchColor = batch.getColor();

		final float color = getImageLayerColor(layer, batchColor);

		final float[] vertices = this.vertices;

		TextureRegion region = layer.getTextureRegion();

		if (region == null) {
			return;
		}

		final float x = layer.getX();
		final float y = layer.getY();
		final float x1 = x * unitScale - viewBounds.x * (layer.getParallaxX() - 1);
		final float y1 = y * unitScale - viewBounds.y * (layer.getParallaxY() - 1);
		final float x2 = x1 + region.getRegionWidth() * unitScale;
		final float y2 = y1 + region.getRegionHeight() * unitScale;

		imageBounds.set(x1, y1, x2 - x1, y2 - y1);

		if (!layer.isRepeatX() && !layer.isRepeatY()) {
			if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
				final float u1 = region.getU();
				final float v1 = region.getV2();
				final float u2 = region.getU2();
				final float v2 = region.getV();

				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = color;
				vertices[U1] = u1;
				vertices[V1] = v1;

				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = color;
				vertices[U2] = u1;
				vertices[V2] = v2;

				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = color;
				vertices[U3] = u2;
				vertices[V3] = v2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = color;
				vertices[U4] = u2;
				vertices[V4] = v1;

				batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
			}
		} else {

			// Determine number of times to repeat image across X and Y, + 4 for padding to avoid pop in/out
			int repeatX = layer.isRepeatX() ? (int)Math.ceil((viewBounds.width / imageBounds.width) + 4) : 0;
			int repeatY = layer.isRepeatY() ? (int)Math.ceil((viewBounds.height / imageBounds.height) + 4) : 0;

			// Calculate the offset of the first image to align with the camera
			float startX = viewBounds.x;
			float startY = viewBounds.y;
			startX = startX - (startX % imageBounds.width);
			startY = startY - (startY % imageBounds.height);

			for (int i = 0; i <= repeatX; i++) {
				for (int j = 0; j <= repeatY; j++) {
					float rx1 = x1;
					float ry1 = y1;
					float rx2 = x2;
					float ry2 = y2;

					// Use (i -2)/(j-2) to begin placing our repeating images outside the camera.
					// In case the image is offset, we must negate this using + (x1% imageBounds.width)
					// It's a way to get the remainder of how many images would fit between its starting position and 0
					if (layer.isRepeatX()) {
						rx1 = startX + ((i - 2) * imageBounds.width) + (x1 % imageBounds.width);
						rx2 = rx1 + imageBounds.width;
					}

					if (layer.isRepeatY()) {
						ry1 = startY + ((j - 2) * imageBounds.height) + (y1 % imageBounds.height);
						ry2 = ry1 + imageBounds.height;
					}

					repeatedImageBounds.set(rx1, ry1, rx2 - rx1, ry2 - ry1);

					if (viewBounds.contains(repeatedImageBounds) || viewBounds.overlaps(repeatedImageBounds)) {
						final float ru1 = region.getU();
						final float rv1 = region.getV2();
						final float ru2 = region.getU2();
						final float rv2 = region.getV();

						vertices[X1] = rx1;
						vertices[Y1] = ry1;
						vertices[C1] = color;
						vertices[U1] = ru1;
						vertices[V1] = rv1;

						vertices[X2] = rx1;
						vertices[Y2] = ry2;
						vertices[C2] = color;
						vertices[U2] = ru1;
						vertices[V2] = rv2;

						vertices[X3] = rx2;
						vertices[Y3] = ry2;
						vertices[C3] = color;
						vertices[U3] = ru2;
						vertices[V3] = rv2;

						vertices[X4] = rx2;
						vertices[Y4] = ry1;
						vertices[C4] = color;
						vertices[U4] = ru2;
						vertices[V4] = rv1;

						batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
					}
				}
			}
		}
	}

	/** Calculates the float color for rendering an image layer, taking into account the layer's tint color, opacity, and whether
	 * the image format supports transparency then multiplying is against the batchColor
	 *
	 * @param layer The layer to render.
	 * @param batchColor The current color of the batch.
	 * @return The float color value to use for rendering. */
	protected float getImageLayerColor (TiledMapImageLayer layer, Color batchColor) {

		final Color combinedTint = layer.getCombinedTintColor();

		// Check if layer supports transparency
		boolean supportsTransparency = layer.supportsTransparency();

		// If the Image Layer supports transparency we do not want to modify the combined tint during rendering
		// and if the Image Layer does not support transparency, we want to multiply the combined tint values, by its alpha
		float alphaMultiplier = supportsTransparency ? 1f : combinedTint.a;
		// Only modify opacity by combinedTint.b if Image Layer supports transparency
		float opacityMultiplier = supportsTransparency ? combinedTint.a : 1f;

		// For image layer rendering multiply all by alpha
		// except for opacity when image layer does not support transparency
		return Color.toFloatBits(batchColor.r * (combinedTint.r * alphaMultiplier),
			batchColor.g * (combinedTint.g * alphaMultiplier), batchColor.b * (combinedTint.b * alphaMultiplier),
			batchColor.a * (layer.getOpacity() * opacityMultiplier));
	}

	/** Calculates the float color for rendering a tile layer, taking into account the layer's tint color and opacity, then
	 * multiplying is against the batchColor
	 *
	 * @param layer
	 * @param batchColor */
	protected float getTileLayerColor (TiledMapTileLayer layer, Color batchColor) {
		return Color.toFloatBits(batchColor.r * layer.getCombinedTintColor().r, batchColor.g * layer.getCombinedTintColor().g,
			batchColor.b * layer.getCombinedTintColor().b, batchColor.a * layer.getCombinedTintColor().a * layer.getOpacity());
	}

	/** Called before the rendering of all layers starts. */
	protected void beginRender () {
		AnimatedTiledMapTile.updateAnimationBaseTime();
		batch.begin();
	}

	/** Called after the rendering of all layers ended. */
	protected void endRender () {
		batch.end();
	}

	@Override
	public void dispose () {
		if (ownsBatch) {
			batch.dispose();
		}
	}
}
