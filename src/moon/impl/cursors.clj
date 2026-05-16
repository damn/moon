(ns moon.impl.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.gdx.app :as app]
            [com.badlogic.gdx.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx Application)))

(defn create
  [{:keys [^Application ctx/app]}]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path [hotspot-x hotspot-y]]]
                   (let [pixmap (pixmap/create (.internal (.getFiles app) (format path-format path)))
                         cursor (app/new-cursor app pixmap hotspot-x hotspot-y)]
                     (pixmap/dispose! pixmap)
                     cursor)))))
