(ns clojure.gdx.scene2d.ui.image
  (:require [clojure.scene2d.actor :as actor]
            clojure.scene2d.ui.image)
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defmulti ^:private create* class)

(defmethod create* Drawable [^Drawable drawable]
  (Image. drawable))

(defmethod create* Texture [^Texture texture]
  (Image. texture))

(defmethod create* TextureRegion [^TextureRegion texture-region]
  (Image. texture-region))

(defn create
  [{:keys [content] :as opts}]
  (doto (create* content)
    (actor/set-opts! opts)))

(extend-type Image
  clojure.scene2d.ui.image/Image
  (set-drawable! [image drawable]
    (.setDrawable image drawable)))
