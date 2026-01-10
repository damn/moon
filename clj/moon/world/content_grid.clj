(ns moon.world.content-grid)

(defprotocol ContentGrid
  (add-entity! [_ eid])
  (remove-entity! [_ eid])
  (position-changed! [_ eid])
  (active-entities [_ center-entity]))
