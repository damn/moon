(ns com.badlogic.gdx.scenes.scene2d.ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defmulti create class)

(defmethod create Texture [texture]
  (Image. ^Texture texture))

(defmethod create TextureRegion [texture-region]
  (Image. ^TextureRegion texture-region))

(defmethod create TextureRegionDrawable [drawable]
  (Image. ^TextureRegionDrawable drawable))

(defn set-drawable! [^Image image drawable]
  (.setDrawable image drawable))
