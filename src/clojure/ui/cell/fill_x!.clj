(ns clojure.ui.cell.fill-x!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/fill-x args))
