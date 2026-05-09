(ns moon.application.create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.files :as files]
            [moon.graphics :as graphics])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap)))

(defn step
  [ctx]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path [hotspot-x hotspot-y]]]
                                           (let [pixmap (Pixmap. ^FileHandle (files/internal ctx (format path-format path)))
                                                 cursor (graphics/new-cursor ctx pixmap hotspot-x hotspot-y)]
                                             (.dispose pixmap)
                                             cursor))))))
