(ns moon.create.cursors
  (:require [clojure.files :as files]
            [clojure.graphics :as graphics]))

(defn step
  [{:keys [ctx/graphics
           ctx/files]
    :as ctx}
   {:keys [data path-format]}]
  (assoc ctx :ctx/cursors
         (update-vals data (fn
                             [[path [hotspot-x hotspot-y]]]
                             (graphics/new-cursor graphics
                                                  (files/internal files (format path-format path))
                                                  hotspot-x
                                                  hotspot-y)))))
