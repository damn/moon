(ns moon.ui.impl.image
  (:require [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.ui.actor :as actor]
            [moon.ui.image :as image])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (cond
         (map? content)
         (Image. (texture-region-drawable/create content))

         (instance? Texture content)
         (Image. ^Texture content)

         (instance? TextureRegion content)
         (Image. ^TextureRegion content)

         :else
         (throw (ex-info "Unkown parameter" {:content content})))
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Image
  image/Image
  (set-drawable! [image drawable]
    (.setDrawable image (texture-region-drawable/create drawable))))
