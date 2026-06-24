(ns editor.widget.animation
  (:require [scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [texture-region.get-region-height :refer [get-region-height]]
            [texture-region.get-region-width :refer [get-region-width]]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.textures :as textures]))

(defn create
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)
                       :let [texture-region (textures/texture-region textures image)
                             scale 2]]
                   {:actor (image-button/create
                            (doto (texture-region-drawable/f texture-region)
                              (set-min-size!/f (* scale (get-region-width texture-region))
                                               (* scale (get-region-height texture-region)))))})]}))
