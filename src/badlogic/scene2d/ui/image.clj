(ns badlogic.scene2d.ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defmulti create class)

(defmethod create Drawable [^Drawable drawable]
  (Image. drawable))

(defmethod create Texture [^Texture texture]
  (Image. texture))

(defmethod create TextureRegion [^TextureRegion texture-region]
  (Image. texture-region))

(defn set-drawable! [^Image image drawable]
  (.setDrawable image drawable))
