(ns clojure.ui.cell.pad-top!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/pad-top args))
