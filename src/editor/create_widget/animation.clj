(ns editor.create-widget.animation
  (:require [gdx.scenes.scene2d.ui.table :as table]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)
                       :let [^TextureRegion texture-region (textures/texture-region textures image)
                             scale 2]]
                   {:actor (ImageButton.
                            (doto (TextureRegionDrawable. texture-region)
                              (.setMinSize (* scale (.getRegionWidth texture-region))
                                           (* scale (.getRegionHeight texture-region)))))})]}))
