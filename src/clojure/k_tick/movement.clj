(ns clojure.k-tick.movement
  (:require [clojure.angle-from-vector :as angle-from-vector]
            [clojure.length :as length]
            [clojure.try-move-solid-body :as try-move-solid-body]
            [clojure.is-nearly-equal :as nearly-equal?]
            [clojure.move :as move]))

(defn f
  [{:keys [direction
           speed
           rotate-in-movement-direction?]
    :as movement}
   eid
   {:keys [ctx/delta-time
           ctx/grid
           ctx/max-speed]}]
  (assert (<= 0 speed max-speed)
          (pr-str speed))
  (assert (vector? direction))
  (assert (or (zero? (length/f direction))
              (nearly-equal?/f 1 (length/f direction)))
          (str "cannot understand direction: " (pr-str direction)))
  (when-not (or (zero? (length/f direction))
                (nil? speed)
                (zero? speed))
    (let [movement (assoc movement :delta-time delta-time)
          body (:entity/body @eid)]
      (when-let [body (if (:body/collides? body)
                        (try-move-solid-body/f grid body (:entity/id @eid) movement)
                        (update body :body/position move/f movement))]
        [[:tx/assoc-in eid [:entity/body :body/position] (:body/position body)]
         (when rotate-in-movement-direction?
           [:tx/assoc-in eid [:entity/body :body/rotation-angle] (angle-from-vector/f direction)])
         [:tx/move-entity eid]]))))
