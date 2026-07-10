(ns gdl.scenes.scene2d.ui.window
  (:refer-clojure :exclude [new class])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(def class
  window/class)

(defn get-title-label [& args]
  (apply window/getTitleLabel args))

(defn get-title-table [& args]
  (apply window/getTitleTable args))

(defn new [& args]
  (apply window/new args))

(defn set-modal! [& args]
  (apply window/setModal args))
