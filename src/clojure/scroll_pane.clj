(ns clojure.scroll-pane
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]))

(defn new [& args]
  (apply scroll-pane/new args))
