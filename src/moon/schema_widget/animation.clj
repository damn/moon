(ns moon.schema-widget.animation
  (:require [moon.schema :as schema]
            [moon.textures :as textures]
            [moon.ui.actor :as actor]))

(defmethod schema/create :s/animation
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
