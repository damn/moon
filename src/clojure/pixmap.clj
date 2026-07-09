(ns clojure.pixmap
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn new [& args]
  (apply pixmap/new args))

(defn set-color! [& args]
  (apply pixmap/set-color! args))

(defn draw-pixel! [& args]
  (apply pixmap/draw-pixel! args))
