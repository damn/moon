(ns editor.create-widget.animation
  (:require [clojure.gdx.image-button.new :as new-image-button]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [gdx.scenes.scene2d.ui.table :as table]
            [moon.textures :as textures]))

(defn- image-button [textures image scale]
  (let [texture-region (textures/texture-region textures image)]
    (new-image-button/f
     (doto (new-texture-region-drawable/f texture-region)
       (set-min-size/f (* scale (get-region-width/f texture-region))
                       (* scale (get-region-height/f texture-region)))))))

(defn f
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (image-button textures image 2)})]}))
