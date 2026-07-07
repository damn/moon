(ns clojure.is-applies-modifiers)

(defn f [[slot _]]
  (not= :inventory.slot/bag slot))
