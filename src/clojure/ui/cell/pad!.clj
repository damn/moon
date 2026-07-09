(ns clojure.ui.cell.pad!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/pad args))
