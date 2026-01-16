(ns moon.create.cursors
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Pixmap)))

(defn- create-cursor [files graphics path [hotspot-x hotspot-y]]
  (let [pixmap (Pixmap. (.internal files path))
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

(defn do!
  [{:keys [ctx/files
           ctx/graphics]
    :as ctx}
   {:keys [data path-format]}]
  (assoc ctx :ctx/cursors
         (update-vals data
                      (fn [[path hotspot]]
                        (create-cursor files
                                       graphics
                                       (format path-format path)
                                       hotspot)))))
