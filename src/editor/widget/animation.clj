(ns editor.widget.animation
  (:require [ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [map.texture-region-drawable :as texture-region-drawable]
            [moon.textures :as textures]))

(defn create
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (image-button/create
                            (texture-region-drawable/create*
                             {:drawable/texture-region (textures/texture-region textures image)
                              :drawable/scale 2}))})]}))
