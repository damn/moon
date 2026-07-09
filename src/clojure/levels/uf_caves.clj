(ns clojure.levels.uf-caves
  (:require [clojure.caves.gen :as caves]
            [clojure.fix-nads :as fix-nads]
            [clojure.texture-region :as texture-region]
            [clojure.create-static-tiled-map-tile :as create-tile]
            [clojure.levels.uf-caves.initial-grid]
            [clojure.levels.uf-caves.fix-nads]
            [clojure.levels.uf-caves.last-steps])
  (:import (java.util Random)))

(defn create
  [world-fn-ctx]
  (let [{:keys [initial-grid-create-fn
                grid2d-fix-nads-fn
                level/creature-properties
                textures
                tile-size
                texture-path
                spawn-rate
                scaling
                cave-size
                cave-style]}
        (merge {:initial-grid-create-fn caves/create
                :grid2d-fix-nads-fn fix-nads/f
                :tile-size 48
                :texture-path "images/uf_terrain.png"
                :spawn-rate 0.02
                :scaling 3
                :cave-size 200
                :cave-style :wide}
               world-fn-ctx)]
    (reduce (fn [m f]
              (f m))
            {:initial-grid-create-fn initial-grid-create-fn
             :grid2d-fix-nads-fn grid2d-fix-nads-fn
             :size cave-size
             :cave-style cave-style
             :random (Random.)
             :level/tile-size tile-size
             :level/create-tile (let [texture (get textures texture-path)]
                                  (memoize
                                   (fn [& {:keys [sprite-idx movement]}]
                                     {:pre [#{"all" "air" "none"} movement]}
                                     (create-tile/f
                                      (texture-region/new texture
                                                        (* (sprite-idx 0) tile-size)
                                                        (* (sprite-idx 1) tile-size)
                                                        tile-size
                                                        tile-size)
                                      "movement" movement))))
             :level/spawn-rate spawn-rate
             :level/scaling scaling
             :level/creature-properties creature-properties}
            [clojure.levels.uf-caves.initial-grid/f
             clojure.levels.uf-caves.fix-nads/f
             clojure.levels.uf-caves.last-steps/step])))
