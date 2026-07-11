(ns moon.grid
  (:require [clojure.grid.find-next-cell :refer [find-next-cell]]
            [clojure.grid.inside-cell :refer [inside-cell?]]
            [clojure.is-occupied-by-other :as occupied-by-other?]
            [clojure.nearest-entity :as nearest-entity]
            [clojure.nearest-entity-distance :as nearest-entity-distance]
            [clojure.update-potential-fields-generate :refer [generate-potential-field]]
            [com.badlogic.gdx.math.circle :as circle]
            [com.badlogic.gdx.math.intersector :as intersector]
            [moon.body :as body]
            [moon.circle :as moon-circle]
            [moon.faction :as faction]
            [moon.g2d :as g2d]
            [moon.rectangle :as rectangle]
            [moon.v2 :as v2]))

(defn entities [cells]
  (into #{} (mapcat :entities) cells))

(defn nearest-enemy [grid entity]
  (nearest-entity/f @(grid (mapv int (:body/position (:entity/body entity))))
                    (faction/enemy (:entity/faction entity))))

(defn nearest-enemy-distance [grid entity]
  (nearest-entity-distance/f @(grid (mapv int (:body/position (:entity/body entity))))
                               (faction/enemy (:entity/faction entity))))

(defn circle->entities [g2d {:keys [position radius] :as circle}]
  (let [[x y] position
        gdx-circle (circle/new x y radius)]
    (->> circle
         moon-circle/outer-rectangle
         rectangle/touched-tiles
         (g2d/get-cells g2d)
         (map deref)
         entities
         (filter #(intersector/overlaps gdx-circle
                                        (body/rectangle (:entity/body @%)))))))

(defn find-direction [grid eid]
  (let [position (:body/position (:entity/body @eid))
        own-cell (grid (mapv int position))
        {:keys [target-entity target-cell]} (find-next-cell grid eid own-cell)]
    (cond
      target-entity
      (v2/direction position (:body/position (:entity/body @target-entity)))

      (nil? target-cell)
      nil

      :else
      (when-not (and (= target-cell own-cell)
                     (occupied-by-other?/f @own-cell eid))
        (when-not (inside-cell? grid @eid target-cell)
          (v2/direction position (:middle @target-cell)))))))

; Assumption: The map contains no not-allowed diagonal cells, diagonal wall cells where both
; adjacent cells are walls and blocked.
; (important for wavefront-expansion and field-following)
; * entities do not move to NADs (they remove them)
; * the potential field flows into diagonals, so they should be reachable too.
;
; TODO assert somewhere/at map load no NAD's and @ potential field init & remove from
; potential-field-following the removal of NAD's.

; TODO remove max pot field movement player screen + 10 tiles as of screen size
; => is coupled to max-steps & also
; to friendly units follow player distance

(defn update!
  [grid pf-cache faction entities max-iterations]
  (let [tiles->entities (let [entities (filter #(= (:entity/faction @%) faction)
                                               entities)]
                          (zipmap (map #(mapv int (:body/position (:entity/body @%))) entities)
                                  entities))
        last-state   [faction :tiles->entities]
        marked-cells [faction :marked-cells]]
    (when-not (= (get-in @pf-cache last-state) tiles->entities)
      (swap! pf-cache assoc-in last-state tiles->entities)
      (doseq [cell (get-in @pf-cache marked-cells)]
        (swap! cell dissoc faction))
      (swap! pf-cache assoc-in marked-cells (generate-potential-field
                                             grid
                                             faction
                                             tiles->entities
                                             max-iterations)))))
