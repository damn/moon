(ns clojure.window
  (:refer-clojure :exclude [new class])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(def class
  window/class)

(defn get-title-label [& args]
  (apply window/get-title-label args))

(defn get-title-table [& args]
  (apply window/get-title-table args))

(defn new [& args]
  (apply window/new args))

(defn set-modal! [& args]
  (apply window/set-modal! args))
