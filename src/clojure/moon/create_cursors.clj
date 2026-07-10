(ns clojure.moon.create-cursors
  (:require [gdl.disposable :as disposable]
            [clojure.edn :as edn]
            [clojure.files :as files]
            [gdl.graphics :as graphics]
            [clojure.java.io :as io]
            [gdl.pixmap :as pixmap]))

(defn f [ctx]
  (assoc ctx
         :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                         (update-vals data
                                      (fn [[path-segment [hotspot-x hotspot-y]]]
                                        (let [path (format path-format path-segment)
                                              pixmap* (pixmap/new (files/internal (:ctx/files ctx) path))
                                              cursor (graphics/new-cursor (:ctx/graphics ctx) pixmap* hotspot-x hotspot-y)]
                                          (disposable/dispose! pixmap*)
                                          cursor))))))
