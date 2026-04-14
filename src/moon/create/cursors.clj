(ns moon.create.cursors
  (:require [gdl.files :as files]
            [gdl.graphics :as graphics]))

(defn- create-cursor
  [{:keys [ctx/files
           ctx/graphics]}
   path-format
   [path [hotspot-x hotspot-y]]]
  (graphics/new-cursor graphics
                       (files/internal files (format path-format path))
                       hotspot-x
                       hotspot-y))

(defn do!
  [ctx {:keys [data path-format]}]
  (assoc ctx :ctx/cursors (update-vals data (partial create-cursor ctx path-format))))
