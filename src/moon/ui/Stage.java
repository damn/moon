package moon.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Stage extends com.badlogic.gdx.scenes.scene2d.Stage  {

  public Object ctx;
  public Object config;

	public Stage (Viewport viewport, Batch batch, Object config) {
		super(viewport, batch);
    this.ctx = null;
    this.config = config;
	}

}
