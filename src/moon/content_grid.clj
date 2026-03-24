(ns moon.content-grid)

(defprotocol ContentGrid
  (add-entity! [this eid])
  (remove-entity! [_ eid])
  (position-changed! [this eid])
  (active-entities [{:keys [grid]} center-entity]))
