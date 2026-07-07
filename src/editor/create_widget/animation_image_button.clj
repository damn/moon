(ns editor.create-widget.animation-image-button
  (:require [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.texture :as texture]
            [clojure.image-button :as image-button]
            [moon.textures :as textures]))

(defn f [textures image scale]
  (let [texture-region (textures/texture-region textures image)]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                       (* scale (texture-region/get-region-height texture-region)))))))
