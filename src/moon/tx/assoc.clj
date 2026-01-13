(ns moon.tx.assoc) ; == add-component to entity ? with entity/create constructors?!

(defn do!
  [_ctx eid k value]
  (swap! eid assoc k value)
  nil)
