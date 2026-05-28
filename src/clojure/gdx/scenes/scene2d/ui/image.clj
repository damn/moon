(ns clojure.gdx.scenes.scene2d.ui.image
  (:require [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defmulti ^:private create* class)

(defmethod create* Texture [texture]
  (Image. ^Texture texture))

(defmethod create* TextureRegion [texture-region]
  (Image. ^TextureRegion texture-region))

(defmethod create* TextureRegionDrawable [drawable]
  (Image. ^TextureRegionDrawable drawable))

(defn create
  [{:keys [content] :as opts}]
  (doto (create* content)
    (actor/set-opts! opts)))

(defn set-drawable! [^Image image drawable]
  (.setDrawable image drawable))
