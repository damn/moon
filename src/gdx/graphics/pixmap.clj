(ns gdx.graphics.pixmap
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn new [file-handle]
  (pixmap/new file-handle))

(defn get-format [pixmap]
  (pixmap/getFormat pixmap))
