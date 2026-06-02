(ns levelgen-test.generate-level
  (:require [clojure.core.edn-resource :refer [edn-resource]]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.gdx.maps.map-layers :as layers]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [clojure.gdx.utils.disposable :as disposable]
            [levelgen-test.show-whole-map :as show-whole-map]
            [moon.creature-tiles]
            [moon.db :as db]))

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
                                                    (db/all-raw db :properties/creatures)
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
        tiled-map/layers
        (layers/get "creatures")
        (layer/set-visible! true))
    (show-whole-map/f! ctx)
    ctx))
