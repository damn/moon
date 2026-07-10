(ns clojure.moon.create-cursors
  (:require [com.badlogic.gdx.utils.disposable :as disposable]
            [clojure.edn :as edn]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics :as graphics]
            [clojure.java.io :as io]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f [ctx]
  (assoc ctx
         :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                         (update-vals data
                                      (fn [[path-segment [hotspot-x hotspot-y]]]
                                        (let [path (format path-format path-segment)
                                              pixmap* (pixmap/new (files/internal (:ctx/files ctx) path))
                                              cursor (graphics/newCursor (:ctx/graphics ctx) pixmap* hotspot-x hotspot-y)]
                                          (disposable/dispose pixmap*)
                                          cursor))))))
