(ns clojure.editor.create-widget-s-animation
  (:require [gdl.texture-region :as texture-region]
            [gdl.texture-region-drawable :as texture-region-drawable]
            [gdl.image-button :as image-button]
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
                         (texture-region-drawable/set-min-size! (* scale (texture-region/getRegionWidth texture-region))
                                                                (* scale (texture-region/getRegionHeight texture-region))))))})]}))
