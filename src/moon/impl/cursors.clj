(ns moon.impl.cursors
  (:require [clojure.files :as files]
            [clojure.graphics :as graphics]))

(defn create
  [{:keys [ctx/graphics
           ctx/files]}
   {:keys [data path-format]}]
  (update-vals data (fn
                      [[path [hotspot-x hotspot-y]]]
                      (graphics/new-cursor graphics
                                           (files/internal files (format path-format path))
                                           hotspot-x
                                           hotspot-y))))
