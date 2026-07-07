(ns clojure.pickup-item
  (:require [clojure.is-stackable :as stackable?]
            [clojure.item-is-valid :as valid?]
            [clojure.can-pickup-item :as can-pickup-item]))

(defn do! [_ctx eid item]
  (assert (valid?/f item))
  (let [[cell cell-item] (can-pickup-item/f? (:entity/inventory @eid) item)]
    (assert cell)
    (assert (or (stackable?/f item cell-item)
                (nil? cell-item)))
    (if (stackable?/f item cell-item)
      (do
       #_(tx/stack-item ctx eid cell item))
      [[:tx/set-item eid cell item]])))
