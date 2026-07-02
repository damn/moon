(ns ctx.cursors
  (:require [clojure.edn :as edn]
            [clojure.gdx.disposable.dispose :as dispose]
            [clojure.gdx.files.internal :as internal]
            [clojure.gdx.pixmap.new :as pixmap]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Graphics)))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (pixmap/f (internal/f files path))
                         cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
                     (dispose/f pixmap)
                     cursor)))))
