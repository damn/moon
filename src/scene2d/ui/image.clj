(ns scene2d.ui.image
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defmulti create class)

(defmethod create Texture [texture]
  ; this(new TextureRegionDrawable(new TextureRegion(texture)));
  ; this(drawable, Scaling.stretch, Align.center);
  (Image. ^Texture texture))

(defmethod create com.badlogic.gdx.graphics.g2d.TextureRegion [texture-region] ; TODO Fuck here is class mentioned
  ; this(new TextureRegionDrawable(region), Scaling.stretch, Align.center);
  (Image. (texture-region/type-hint texture-region)))

(defmethod create TextureRegionDrawable [drawable]
  (Image. ^TextureRegionDrawable drawable))

(comment
 (defn f [drawable scaling align]
   (Image. drawable scaling align))
 )
