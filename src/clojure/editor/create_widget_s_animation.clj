(ns clojure.editor.create-widget-s-animation
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.moon-textures :as textures]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.ui-table :as table]))

(defmethod create-widget :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor
                    (let [scale 2
                          texture-region (textures/texture-region textures image)]
                      (image-button/new
                       (doto (texture-region-drawable/new texture-region)
                         (texture-region-drawable/setMinSize (* scale (texture-region/getRegionWidth texture-region))
                                                                (* scale (texture-region/getRegionHeight texture-region))))))})]}))
