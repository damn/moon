package scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Stage extends com.badlogic.gdx.scenes.scene2d.Stage {

  public Object ctx;

	public Stage (Viewport viewport, Batch batch) {
		super(viewport, batch);
    this.ctx = null;
	}

}
