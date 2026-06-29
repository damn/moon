(ns editor.create-widget.animation
  (:require [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)))

(defn f
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)
                       :let [texture-region (textures/texture-region textures image)
                             scale 2]]
                   {:actor (ImageButton.
                            (doto (texture-region-drawable/f texture-region)
                              (set-min-size!/f (* scale (TextureRegion/.getRegionWidth texture-region))
                                               (* scale (TextureRegion/.getRegionHeight texture-region)))))})]}))
