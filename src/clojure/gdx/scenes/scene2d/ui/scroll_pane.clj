(ns clojure.gdx.scenes.scene2d.ui.scroll-pane
  (:require [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]))

(defn create [widget skin]
  (scroll-pane/new widget skin))
