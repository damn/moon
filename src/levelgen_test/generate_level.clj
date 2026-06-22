(ns levelgen-test.generate-level
  (:require [clojure.edn-resource :refer [edn-resource]]
            [gdl.texture-region :as texture-region]
            [gdl.get-layer :refer [get-layer]]
            [gdl.get-layers :refer [get-layers]]
            [gdl.tiled-map-tile-layer.set-visible :refer [set-visible!]]
            [gdl.dispose :as disposable]
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
                                                          (texture-region/f texture x y w h)
                                                          (texture-region/f texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        get-layers
        (get-layer "creatures")
        (set-visible! true))
    (show-whole-map/f! ctx)
    ctx))
