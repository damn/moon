(ns com.badlogic.gdx.scenes.scene2d.ui.image
  (:require [gdl.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.ui.image :as image])
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

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (create* (if (map? content)
                   (texture-region-drawable/create content)
                   content))
    (actor/set-opts! opts)))

(extend-type Image
  image/Image
  (set-drawable! [image drawable]
    (.setDrawable image (texture-region-drawable/create drawable))))
