(ns moon.impl.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.graphics :as graphics])
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics Pixmap)))

(defn create
  [{:keys [^Application ctx/app]}]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path [hotspot-x hotspot-y]]]
                   (let [pixmap (Pixmap. (.internal (.getFiles app) (format path-format path)))
                         cursor (graphics/new-cursor app pixmap hotspot-x hotspot-y)]
                     (.dispose pixmap)
                     cursor)))))
