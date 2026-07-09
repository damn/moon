(ns clojure.ui.cell.expand-x!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/expand-x args))
