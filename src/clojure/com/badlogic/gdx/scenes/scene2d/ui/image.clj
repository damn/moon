(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create-from-texture [^Texture texture]
  (Image. texture))

(defn create-from-drawable [^Drawable drawable]
  (Image. drawable))

(defn create-from-texture-region [^TextureRegion texture-region]
  (Image. texture-region))

(defn set-drawable! [^Image image ^Drawable drawable]
  (.setDrawable image drawable))
