(ns com.badlogic.gdx.scenes.scene2d.ui.image
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn new [^TextureRegion texture-region]
  (Image. texture-region))

(defn new-drawable [^Drawable drawable]
  (Image. drawable))

(defn new-texture [^Texture texture]
  (Image. texture))

(defn set-drawable! [^Image image ^Drawable drawable]
  (Image/.setDrawable image drawable))
