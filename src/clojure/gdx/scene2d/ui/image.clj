(ns clojure.gdx.scene2d.ui.image
  (:require [clojure.gdx.scene2d.actor :as actor])
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

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (create* content)
    (actor/set-opts! opts)))

(defn set-drawable! [^Image image drawable]
  (.setDrawable image drawable))
