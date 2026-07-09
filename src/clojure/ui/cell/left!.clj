(ns clojure.ui.cell.left!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/left args))
