(ns moon.ui.image
  (:require [gdl.ui.image :as image]
            [gdl.ui.widget :as widget]
            [moon.ui.actor :as actor]
            [moon.utils.align :as align]
            [moon.utils.scaling :as scaling])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defmulti create* type)

(defmethod create* Drawable [^Drawable drawable]
  (Image. drawable))

(defmethod create* Texture [texture]
  (Image. ^Texture texture))

(defmethod create* TextureRegion [texture-region]
  (Image. ^TextureRegion texture-region))

(defn create
  [{:keys [image/object
           scaling
           align
           fill-parent?]
    :as opts}]
  (let [image (create* object)]
    (when (= :center align)
      (image/set-align! image align/center))
    (when (= :fill scaling)
      (image/set-scaling! image scaling/fill))
    (when fill-parent?
      (widget/set-fill-parent! image true))
    (actor/set-opts! image opts)))
