(ns moon.create.cursors
  (:require [clj.api.com.badlogic.gdx.files :as files]
            [clj.api.com.badlogic.gdx.graphics :as graphics]
            [clj.api.com.badlogic.gdx.graphics.pixmap :as pixmap]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]))

(defn- create-cursor
  [{:keys [ctx/files
           ctx/graphics]}
   path-format
   [path [hotspot-x hotspot-y]]]
  (let [pixmap (pixmap/create (files/internal files (format path-format path)))
        cursor (graphics/new-cursor graphics pixmap hotspot-x hotspot-y)]
    (disposable/dispose! pixmap)
    cursor))

(defn do!
  [ctx {:keys [data path-format]}]
  (assoc ctx :ctx/cursors (update-vals data (partial create-cursor ctx path-format))))
