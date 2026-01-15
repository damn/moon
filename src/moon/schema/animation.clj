(ns moon.schema.animation
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.graphics :as graphics]
            [moon.schema :as schema]
            [moon.schemas :as schemas]
            [moon.ui.image-button :as image-button]))

(defmethod schema/malli-form :s/animation [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

(defmethod schema/create :s/animation
  [_ animation {:keys [ctx/graphics
                       ctx/skin]}]
  (table/create
   {:rows [(for [image (:animation/frames animation)]
             {:actor (image-button/create
                      {:drawable/texture-region (graphics/texture-region graphics image)
                       :drawable/scale 2
                       :skin skin})})]
    :cell-defaults {:pad 1}}))
