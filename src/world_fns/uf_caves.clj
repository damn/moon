(ns world-fns.uf-caves
  (:require [clojure.gdx.graphics.texture :as texture]
            [clojure.grid2d :as g2d]
            [clojure.gdx.maps.tiled.tiles.static-tiled-map-tile.create :as create-tile]
            [world-fns.uf-caves.last-steps]))

(defn- initial-grid
  [{:keys [initial-grid-create-fn
           size
           cave-style
           random]
    :as level}]
  (let [{:keys [start grid]} (initial-grid-create-fn random size size cave-style)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    (assoc level
           :level/start start
           :level/grid grid)))

(defn- fix-nads
  [{:keys [level/grid]
    :as level}]
  (let [grid ((:grid2d-fix-nads-fn level) grid)]
    (assoc level :level/grid grid)))

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
           :random (java.util.Random.)
           :level/tile-size tile-size
           :level/create-tile (let [texture (get textures texture-path)]
                                (memoize
                                 (fn [& {:keys [sprite-idx movement]}]
                                   {:pre [#{"all" "air" "none"} movement]}
                                   (create-tile/f
                                    (texture/region texture
                                                    (* (sprite-idx 0) tile-size)
                                                    (* (sprite-idx 1) tile-size)
                                                    tile-size
                                                    tile-size)
                                    "movement" movement))))
           :level/spawn-rate spawn-rate
           :level/scaling scaling
           :level/creature-properties creature-properties}
          [initial-grid
           fix-nads
           world-fns.uf-caves.last-steps/step]))
