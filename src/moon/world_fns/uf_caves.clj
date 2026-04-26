(ns moon.world-fns.uf-caves
  (:require [clojure.graphics.texture :as texture]
            [moon.tiled-map :as tiled-map]
            moon.uf-caves.initial-grid
            moon.uf-caves.fix-nads
            moon.uf-caves.last-steps))

(defn create
  [{:keys [level/creature-properties
           textures
           tile-size
           texture-path
           spawn-rate
           scaling
           cave-size
           cave-style]}]
  (reduce (fn [m f]
            (f m))
          {:size cave-size
           :cave-style cave-style
           :random (java.util.Random.)
           :level/tile-size tile-size
           :level/create-tile (let [texture (get textures texture-path)]
                                (memoize
                                 (fn [& {:keys [sprite-idx movement]}]
                                   {:pre [#{"all" "air" "none"} movement]}
                                   (tiled-map/static-tiled-map-tile
                                    (texture/region texture
                                                    (* (sprite-idx 0) tile-size)
                                                    (* (sprite-idx 1) tile-size)
                                                    tile-size
                                                    tile-size)
                                    "movement" movement))))
           :level/spawn-rate spawn-rate
           :level/scaling scaling
           :level/creature-properties creature-properties}
          [moon.uf-caves.initial-grid/step
           moon.uf-caves.fix-nads/step
           moon.uf-caves.last-steps/step]))
