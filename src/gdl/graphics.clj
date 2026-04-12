(ns gdl.graphics
  (:require [clj.api.com.badlogic.gdx.graphics.pixmap :as pixmap]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable])
  (:import (com.badlogic.gdx.graphics GL20)))

(defprotocol Graphics
  (new-cursor [_ pixmap hotspot-x hotspot-y])
  (frames-per-second [_])
  (delta-time [_])
  (set-cursor! [_ cursor]))

(defn clear! [^com.badlogic.gdx.Graphics graphics r g b a]
  (.glClearColor (.getGL20 graphics) r g b a)
  (.glClear      (.getGL20 graphics) GL20/GL_COLOR_BUFFER_BIT))

(defn new-cursor* [graphics file-handle hotspot-x hotspot-y]
  (let [pixmap (pixmap/create file-handle)
        cursor (new-cursor graphics pixmap hotspot-x hotspot-y)]
    (disposable/dispose! pixmap)
    cursor))
