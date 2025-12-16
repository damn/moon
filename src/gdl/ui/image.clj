(ns gdl.ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defmulti create type)

(defmethod create Drawable [^Drawable drawable]
  (Image. drawable))

(defmethod create Texture [texture]
  (Image. ^Texture texture))

(defmethod create TextureRegion [texture-region]
  (Image. ^TextureRegion texture-region))

(defn set-drawable! [image drawable]
  (Image/.setDrawable image drawable))

(defn set-align! [image align]
  (Image/.setAlign image align))

(defn set-scaling! [image scaling]
  (Image/.setScaling image scaling))
