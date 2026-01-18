(ns moon.schema.animation
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.schemas :as schemas]
            [moon.textures :as textures]
            [moon.ui.image-button :as image-button]))

(defn malli-form [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

(defn create
  [_ animation {:keys [ctx/skin
                       ctx/textures]}]
  (table/create
   {:rows [(for [image (:animation/frames animation)]
             {:actor (image-button/create
                      {:drawable/texture-region (textures/texture-region textures image)
                       :drawable/scale 2
                       :skin skin})})]
    :cell-defaults {:pad 1}}))
