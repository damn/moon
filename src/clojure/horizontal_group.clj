(ns clojure.horizontal-group
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]))

(defn new [& args]
  (apply horizontal-group/new args))

(defn pad! [& args]
  (apply horizontal-group/pad args))

(defn space! [& args]
  (apply horizontal-group/space args))
