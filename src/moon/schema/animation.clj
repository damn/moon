(ns moon.schema.animation
  (:require [gdl.texture-region :as texture-region]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
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
                        {:actor (let [texture-region (textures/texture-region textures image)]
                                  (image-button/create (doto (texture-region-drawable/create texture-region)
                                                         (drawable/set-min-size! (* 2 (texture-region/width texture-region))
                                                                                 (* 2 (texture-region/height texture-region))))))})])))
