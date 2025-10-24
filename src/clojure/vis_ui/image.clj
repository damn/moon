(ns clojure.vis-ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)
           (com.kotcrab.vis.ui.widget VisImage)))

(defmulti create type)

(defmethod create Drawable [^Drawable drawable]
  (VisImage. drawable))

(defmethod create Texture [texture]
  (VisImage. (TextureRegion. ^Texture texture)))

(defmethod create TextureRegion [texture-region]
  (VisImage. ^TextureRegion texture-region))
