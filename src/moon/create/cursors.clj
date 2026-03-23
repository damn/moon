(ns moon.create.cursors
  (:import (com.badlogic.gdx Files
                             Graphics)
           (com.badlogic.gdx.graphics Pixmap)))

(defn- create-cursor
  [{:keys [^Files ctx/files
           ctx/graphics]}
   path-format
   [path [hotspot-x hotspot-y]]]
  (let [pixmap (Pixmap. (.internal files (format path-format path)))
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

(defn do!
  [ctx {:keys [data path-format]}]
  (assoc ctx :ctx/cursors (update-vals data (partial create-cursor ctx path-format))))
