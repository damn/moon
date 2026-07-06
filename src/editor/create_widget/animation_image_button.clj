(ns editor.create-widget.animation-image-button
  (:require
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture] [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [moon.textures :as textures]))

(defn f [textures image scale]
  (let [texture-region (textures/texture-region textures image)]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/set-min-size! (* scale (get-region-width/f texture-region))
                       (* scale (get-region-height/f texture-region)))))))
