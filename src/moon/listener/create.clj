(ns moon.listener.create
  (:require [qrecord.core :as q]))

(q/defrecord Context [])

(defn do!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params)) ; this is good, you separated the function in sub-parts, simplieifieng an abstraction
          (merge (map->Context {})
                 {
                  :ctx/unit-scale (atom 1)
                  :ctx/factions-iterations {:good 15 :evil 5}
                  :ctx/max-delta 0.04
                  :ctx/minimum-size 0.39
                  :ctx/z-orders [:z-order/on-ground
                                 :z-order/ground
                                 :z-order/flying
                                 :z-order/effect]
                  })
          create-fns))

; TODO does too much, just pipelineing?
