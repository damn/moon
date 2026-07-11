(ns clojure.levels.modules
  (:require [clojure.grid-to-tiled-map :refer [grid->tiled-map]]
            [clojure.java.util.random :as random]
            [clojure.levels.modules.place-step :as place-step]
            [clojure.load-tmx-map :as load-tmx-map]
            [clojure.movement-property :as movement-property]
            [clojure.property-value :refer [property-value]]
            [clojure.tiled-map.add-creatures-layer :as add-creatures-layer]
            [clojure.tiled-tiled-map :as tiled-tiled-map]
            [com.badlogic.gdx.maps.map-layers :as map-layers]
            [com.badlogic.gdx.maps.tiled.tiled-map :as gdx-tiled-map]
            [moon.caves :as caves]
            [moon.g2d :as g2d]))

(defn print-grid [{:keys [grid] :as world-fn-ctx}]
  (g2d/print-y-up grid)
  (println " - ")
  world-fn-ctx)

(defn- initial-grid
  [{:keys [initial-grid-fn
           grid2d-fix-nads-fn
           world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (initial-grid-fn (random/new-random) map-size map-size :wide)
        grid (grid2d-fix-nads-fn grid)]
    (assoc world-fn-ctx
           :start start
           :grid grid)))

(defn- assoc-transitions
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (g2d/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition} (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    (assoc world-fn-ctx :grid grid)))

(defn- create-scaled-grid [w]
  (assoc w :scaled-grid (g2d/scale-by (:grid w) (:scale w))))

(defn- load-schema-tiled-map [w]
  (assoc w :schema-tiled-map (load-tmx-map/f "maps/modules.tmx")))

(defn- place-modules
  [{:keys [scale
           scaled-grid
           grid
           schema-tiled-map]
    :as w}]
  (assoc w :scaled-grid (place-step/f schema-tiled-map
                                      scale
                                      scaled-grid
                                      grid
                                      (filter #(= :ground (get grid %)) (g2d/posis grid))
                                      (filter #(= :transition (get grid %)) (g2d/posis grid)))))

(defn- convert-to-tiled-map
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (tiled-tiled-map/f (grid->tiled-map schema-tiled-map scaled-grid))))

(defn- calculate-start [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))

(defn- last-steps
  [{:keys [world/max-area-level
           world/spawn-rate
           level/creature-properties
           grid
           start
           scale
           scaled-grid
           tiled-map
           start-position]}]
  (let [can-spawn? #(= "all" (movement-property/f tiled-map %))
        _ (assert (can-spawn? start-position))
        spawn-positions (g2d/flood-fill scaled-grid start-position can-spawn?)
        {:keys [_steps area-level-grid]} (g2d/area-level-grid
                                          :grid grid
                                          :start start
                                          :max-level max-area-level
                                          :walk-on #{:ground :transition})
        _ (assert (or
                   (= (set (concat [max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))
                   (= (set (concat [:wall max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))))
        scaled-area-level-grid (g2d/scale-by area-level-grid scale)
        get-free-position-in-area-level (fn [area-level]
                                          (rand-nth
                                           (filter
                                            (fn [p]
                                              (and (= area-level (get scaled-area-level-grid p))
                                                   (#{:no-cell :undefined}
                                                    (property-value (map-layers/get (gdx-tiled-map/getLayers tiled-map) "creatures")
                                                                    p
                                                                    "id"))))
                                            spawn-positions)))
        creatures (for [position spawn-positions
                        :let [area-level (get scaled-area-level-grid position)
                              creatures (filter #(= area-level (:creature/level %))
                                                creature-properties)]
                        :when (and (number? area-level)
                                   (<= (rand) spawn-rate)
                                   (seq creatures))]
                    [position (rand-nth creatures)])]
    (add-creatures-layer/f tiled-map creatures)
    {:tiled-map tiled-map
     :start-position (get-free-position-in-area-level 0)
     :area-level-grid scaled-area-level-grid}))

(defn create
  [world-fn-ctx]
  (let [world-fn-ctx (merge {:initial-grid-fn caves/create
                             :grid2d-fix-nads-fn g2d/fix-nads
                             :world/map-size 5
                             :world/max-area-level 3
                             :world/spawn-rate 0.05}
                            world-fn-ctx)
        {:keys [world/map-size
                world/max-area-level]} world-fn-ctx]
    (assert (<= max-area-level map-size))
    (-> world-fn-ctx
        (assoc :scale [32 20])
        initial-grid
        #_print-grid
        assoc-transitions
        #_print-grid
        create-scaled-grid
        load-schema-tiled-map
        place-modules
        convert-to-tiled-map
        calculate-start
        last-steps)))
