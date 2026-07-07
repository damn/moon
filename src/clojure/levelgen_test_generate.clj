(ns clojure.levelgen-test-generate
  (:require [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.edn-resource :refer [edn-resource]]
            [clojure.get :as get]
            [clojure.get-property :as get-property]
            [clojure.zoom-to-rect :as zoom-to-rect]
            [clojure.creature-tiles :as creature-tiles]
            [clojure.all-raw :refer [all-raw]]
            [clojure.moon-textures :as textures]
            [clojure.orthographic-camera-set-position :refer [set-position!]]))

; TODO does too many things!
(defn f
  [{:keys [ctx/camera
           ctx/db
           ctx/textures]
    :as ctx}
   level-fn]
  (let [level (let [[f params] (edn-resource level-fn)]
                (f
                 (assoc params
                        :level/creature-properties (creature-tiles/prepare
                                                    (all-raw db :properties/creatures)
                                                    #(textures/texture-region textures %))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        tiled-map/get-layers
        (get/f "creatures")
        (tiled-map-tile-layer/set-visible! true))
    (set-position! camera [(/ (get-property/f tiled-map "width") 2)
                           (/ (get-property/f tiled-map "height") 2)])
    (zoom-to-rect/f camera {:left [0 0]
                            :top [0 (get-property/f tiled-map "height")]
                            :right [(get-property/f tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))
