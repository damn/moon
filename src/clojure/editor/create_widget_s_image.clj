(ns clojure.editor.create-widget-s-image
  (:require [clojure.editor.create-widget :refer [create-widget]]
            [clojure.image-button :as image-button]
            [clojure.moon-textures :as textures]
            [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]))

(defmethod create-widget :s/image
  [_ image {:keys [ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)
        scale 2]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                                              (* scale (texture-region/get-region-height texture-region)))))))
