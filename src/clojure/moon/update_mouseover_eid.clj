(ns clojure.moon.update-mouseover-eid
  (:require [clojure.line-of-sight :as line-of-sight?]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.point-to-entities :refer [point->entities]]
            [clojure.sort-by-order :as sort-by-order]))

(defn f
  [{:keys [ctx/mouseover-eid
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [position world-mouse-position
        new-eid (if (mouseover-actor ctx)
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (point->entities grid position))]
                    (->> render-z-order
                         (sort-by-order/f hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(line-of-sight?/f raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))
