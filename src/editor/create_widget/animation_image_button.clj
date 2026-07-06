(ns editor.create-widget.animation-image-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [moon.textures :as textures]))

(defn f [textures image scale]
  (let [texture-region (textures/texture-region textures image)]
    (image-button/new
     (doto (new-texture-region-drawable/f texture-region)
       (set-min-size/f (* scale (get-region-width/f texture-region))
                       (* scale (get-region-height/f texture-region)))))))
