(ns moon.ui.image
  (:require [gdl.ui.widget :as widget]
            [gdl.utils.align :as align]
            [gdl.utils.scaling :as scaling]
            [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defmulti ^:private create* type)

(defmethod create* Drawable [^Drawable drawable]
  (Image. drawable))

(defmethod create* Texture [texture]
  (Image. ^Texture texture))

(defmethod create* TextureRegion [texture-region]
  (Image. ^TextureRegion texture-region))

(defn set-drawable! [image ^Drawable drawable]
  (.setDrawable image drawable))

(defn create
  [{:keys [image/object
           scaling
           align
           fill-parent?]
    :as opts}]
  (let [image (create* object)]
    (when (= :center align)
      (Image/.setAlign image align/center))
    (when (= :fill scaling)
      (Image/.setScaling image scaling/fill))
    (when fill-parent?
      (widget/set-fill-parent! image true))
    (actor/set-opts! image opts)))
