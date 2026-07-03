(ns levelgen-test.generate
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.gdx.map-layers.get :as get]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [clojure.gdx.tiled-map-tile-layer.set-visible :as set-visible]
            [levelgen-test.get-property :as get-property]
            [levelgen-test.zoom-to-rect :as zoom-to-rect]
            [moon.creature-tiles :as creature-tiles]
            [moon.db.all-raw :refer [all-raw]]
            [moon.textures :as textures]
            [orthographic-camera.set-position :refer [set-position!]]))

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
        get-layers/f
        (get/f "creatures")
        (set-visible/f true))
    (set-position! camera [(/ (get-property/f tiled-map "width") 2)
                           (/ (get-property/f tiled-map "height") 2)])
    (zoom-to-rect/f camera {:left [0 0]
                            :top [0 (get-property/f tiled-map "height")]
                            :right [(get-property/f tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))
