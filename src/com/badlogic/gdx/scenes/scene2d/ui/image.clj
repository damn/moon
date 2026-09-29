(ns com.badlogic.gdx.scenes.scene2d.ui.image
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn new [^TextureRegion texture-region]
  (Image. texture-region))

(defn newDrawable [^Drawable drawable]
  (Image. drawable))

(defn newTexture [^Texture texture]
  (Image. texture))

(defn setDrawable [^Image image ^Drawable drawable]
  (.setDrawable image drawable))
