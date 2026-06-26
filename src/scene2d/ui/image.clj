(ns scene2d.ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defmulti create class)

(defmethod create Texture [texture]
  ; this(new TextureRegionDrawable(new TextureRegion(texture)));
  ; this(drawable, Scaling.stretch, Align.center);
  (Image. ^Texture texture))

(defmethod create TextureRegion [texture-region]
  ; this(new TextureRegionDrawable(region), Scaling.stretch, Align.center);
  (Image. ^TextureRegion texture-region))

(defmethod create TextureRegionDrawable [drawable]
  (Image. ^TextureRegionDrawable drawable))

(comment
 (defn f [drawable scaling align]
   (Image. drawable scaling align))
 )
