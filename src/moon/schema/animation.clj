(ns moon.schema.animation
  (:require [moon.schemas :as schemas]
            [moon.textures :as textures]
            [clojure.scene2d.actor :as actor]))

(defn malli-form [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

(defn create
  [_ animation {:keys [ctx/skin
                       ctx/textures]}]
  (actor/create
   {:type :ui/table
    :table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (actor/create
                            {:type :ui/image-button
                             :drawable {:drawable/texture-region (textures/texture-region textures image)
                                        :drawable/scale 2}})})]}))
