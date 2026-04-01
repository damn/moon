(ns moon.schema.animation
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [moon.image-button :as image-button]
            [moon.schemas :as schemas]
            [moon.table :as table]
            [moon.textures :as textures]))

(defn malli-form [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

(defn create
  [_ animation {:keys [ctx/skin
                       ctx/textures]}]
  (doto (gdx-table/create)
    (table/set-cell-defaults! {:pad 1})
    (table/add-rows! [(for [image (:animation/frames animation)]
                        {:actor (image-button/create
                                 {:drawable/texture-region (textures/texture-region textures image)
                                  :drawable/scale 2
                                  :skin skin})})])))
