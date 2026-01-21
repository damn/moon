(ns moon.create.cursors
  (:import (com.badlogic.gdx Files
                             Graphics)
           (com.badlogic.gdx.graphics Pixmap)))

(defn do!
  [{:keys [^Files ctx/files
           ctx/graphics]
    :as ctx}
   {:keys [data path-format]}]
  (assoc ctx :ctx/cursors
         (update-vals data
                      (fn [[path [hotspot-x hotspot-y]]]
                        (let [pixmap (Pixmap. (.internal files (format path-format path)))
                              cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
                          (.dispose pixmap)
                          cursor)))))
