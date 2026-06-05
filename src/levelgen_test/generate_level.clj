(ns levelgen-test.generate-level
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.texture :as texture]
            [clojure.maps.map-layers :as layers]
            [clojure.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [clojure.maps.tiled.tiled-map-tile-layer.set-visible :refer [set-visible!]]
            [clojure.gdx.utils.disposable :as disposable]
            [levelgen-test.show-whole-map :as show-whole-map]
            [moon.creature-tiles]
            [moon.db.all-raw :refer [all-raw]]))

(defn f
  [{:keys [ctx/db
           ctx/textures
           ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (disposable/dispose! tiled-map))
  (let [level (let [[f params] (edn-resource level-fn)]
                (f
                 (assoc params
                        :level/creature-properties (moon.creature-tiles/prepare
                                                    (all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (texture/region texture x y w h)
                                                          (texture/region texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        get-layers
        (layers/get "creatures")
        (set-visible! true))
    (show-whole-map/f! ctx)
    ctx))
