(ns gdx.actor.widget.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create [drawable]
  (Image. ^TextureRegion drawable))

(defn create-from-texture [texture]
  (Image. ^Texture texture))

(defn create-drawable [drawable]
  (Image. ^Drawable drawable))

(defn set-drawable! [image drawable]
  (.setDrawable ^Image image ^Drawable drawable))
