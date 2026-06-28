(ns levelgen-test.generate-level
  (:require [clojure.edn-resource :refer [edn-resource]]
            [tiled-map.get-layers :refer [get-layers]]
            [tiled-map-tile-layer.set-visible :refer [set-visible!]]
            [levelgen-test.show-whole-map :as show-whole-map]
            [moon.creature-tiles]
            [moon.db.all-raw :refer [all-raw]])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx.utils Disposable)))

(defn f
  [{:keys [ctx/db
           ctx/textures
           ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (Disposable/.dispose tiled-map))
  (let [level (let [[f params] (edn-resource level-fn)]
                (f
                 (assoc params
                        :level/creature-properties (moon.creature-tiles/prepare
                                                    (all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [^Texture texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (TextureRegion. texture (int x) (int y) (int w) (int h))
                                                          (TextureRegion. texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        get-layers
        (MapLayers/.get "creatures")
        (set-visible! true))
    (show-whole-map/f! ctx)
    ctx))
