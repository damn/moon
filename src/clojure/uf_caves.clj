(ns clojure.uf-caves
  (:require [clojure.texture-region :as texture-region]
            [clojure.create-static-tiled-map-tile :as create-tile]
            [clojure.uf-caves-initial-grid]
            [clojure.uf-caves-fix-nads]
            [clojure.uf-caves-last-steps])
  (:import (java.util Random)))

(defn create
  [{:keys [initial-grid-create-fn
           grid2d-fix-nads-fn
           level/creature-properties
           textures
           tile-size
           texture-path
           spawn-rate
           scaling
           cave-size
           cave-style]}]
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
          [clojure.uf-caves-initial-grid/f
           clojure.uf-caves-fix-nads/f
           clojure.uf-caves-last-steps/step]))
