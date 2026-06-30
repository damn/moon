(ns levelgen-test.generate-level
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.gdx :as gdx]
            [levelgen-test.show-whole-map :as show-whole-map]
            [moon.creature-tiles]
            [moon.db.all-raw :refer [all-raw]]))

(defn f
  [{:keys [ctx/db
           ctx/textures
           ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (gdx/dispose! tiled-map))
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
                                                          (gdx/texture-region texture (int x) (int y) (int w) (int h))
                                                          (gdx/texture-region texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> (gdx/map-layers-get (gdx/tiled-map-get-layers tiled-map) "creatures")
        (gdx/tiled-map-tile-layer-set-visible! true))
    (show-whole-map/f! ctx)
    ctx))
